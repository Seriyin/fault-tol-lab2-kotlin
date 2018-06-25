package pt.um.tf.lab2.lab2srv

import io.atomix.catalyst.concurrent.SingleThreadContext
import io.atomix.catalyst.serializer.Serializer
import mu.KLogging
import pt.haslab.ekit.Spread
import pt.um.tf.lab2.lab2mes.Message
import pt.um.tf.lab2.lab2mes.NewMessage
import pt.um.tf.lab2.lab2mes.Reply
import pt.um.tf.lab2.lab2mes.UpdateMessage
import spread.SpreadGroup
import spread.SpreadMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom


fun main(args : Array<String>) {
    val main = Main(if (args.isNotEmpty()) args[0].toBoolean() else false)
    main.run()
}

class Main(s: Boolean) {
    companion object : KLogging()

    val tlr = ThreadLocalRandom.current()
    val me = UUID.randomUUID().toString()
    val sr = Serializer()
    val tc = SingleThreadContext("srv-%d",sr)
    val acc = Account()
    val spread = Spread("srv-$me", false)
    var phase: Phase = if (s) Phase.first() else Phase.initPhase()
    val waitQueue : Queue<Message> by lazy {
        ArrayDeque<Message>()
    }
    lateinit var spreadGroup : SpreadGroup

    fun run() {
        sr.register(Message::class.java)
        sr.register(Reply::class.java)
        sr.register(NewMessage::class.java)
        sr.register(UpdateMessage::class.java)
        tc.execute(this::openAndJoin)
          .thenRun(this::handlers)
          .thenRun(this::sendNew)
        while(readLine() == null);
        spread.leave(spreadGroup)
        spread.close()
        tc.close()
        logger.info("I'm here")
    }

    private fun openAndJoin() {
        spread.open()
        spreadGroup = spread.join("banks")
        logger.info { "Joined : $spreadGroup" }
    }

    private fun sendNew() {
        val sm = SpreadMessage()
        sm.setCausal()
        sm.addGroup("banks")
        spread.multicast(sm, NewMessage(me))
    }

    private fun handlers() {
        logger.info("Handling connection")
        spread.handler(Message::class.java, this@Main::handler)
              .handler(Reply::class.java, this@Main::handler)
              .handler(NewMessage::class.java, this@Main::handler)
              .handler(UpdateMessage::class.java, this@Main::handler)
    }

    private fun handler(sm : SpreadMessage, m: Reply) {
        logger.error("Reply : $m")
    }

    private fun handler(sm: SpreadMessage,
                        m: NewMessage) {
        when (phase) {
            Phase.UP -> {
                logger.info {
                    "New message from ${sm.sender}"
                }
                val spm = SpreadMessage()
                spm.addGroup(sm.sender)
                spm.setCausal()
                spread.multicast(spm, UpdateMessage(acc.balance()))
            }
            Phase.UNK -> {
                if (m.origin == me) {
                    phase = phase.change()
                    logger.info { "Now waiting for update : $phase" }
                }
            }
            Phase.WAIT -> {}
        }

    }

    private fun handler(sm: SpreadMessage, m: UpdateMessage) {
        when (phase) {
            Phase.WAIT -> {
                acc.movement(m.accbalance)
                waitQueue.forEach {
                    logger.info { "Processing from ${it.origin} op : ${it.op}" }
                    if(it.op == 1) {
                        acc.movement(it.mov)
                    }
                }
                waitQueue.clear()
                phase = phase.change()
                logger.info { "Now up-to-date : $phase" }
            }
            Phase.UP -> {}
            Phase.UNK -> {}
        }
    }

    private fun handler(sm : SpreadMessage,
                        m : Message) {
        when (phase) {
            Phase.WAIT -> {
                waitQueue.add(m)
            }
            Phase.UP -> {
                logger.info { "Got message with op : ${m.op} from ${m.origin}" }
                val spm = SpreadMessage()
                spm.addGroup(sm.sender)
                spm.setSafe()
                when (m.op) {
                    1 -> {
                        spread.multicast(spm, Reply(m.seq,1, acc.movement(m.mov), acc.balance()))
                    }
                    0 -> {
                        spread.multicast(spm, Reply(m.seq,0, false, acc.balance()))
                    }
                    else -> {
                        logger.error { InputMismatchException() }
                    }
                }
            }
            Phase.UNK -> {}
        }
    }

}



