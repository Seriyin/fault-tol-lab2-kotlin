package pt.um.tf.lab2.lab2srv

import io.atomix.catalyst.concurrent.SingleThreadContext
import io.atomix.catalyst.serializer.Serializer
import io.atomix.catalyst.transport.Address
import io.atomix.catalyst.transport.Transport
import io.atomix.catalyst.transport.netty.NettyTransport
import pt.haslab.ekit.Spread
import pt.um.tf.lab2.lab2mes.Message
import pt.um.tf.lab2.lab2mes.NewMessage
import pt.um.tf.lab2.lab2mes.Reply
import spread.SpreadMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom


fun main(args : Array<String>) {
    val main = Main()
    main.run()
}

class Main {
    val me = Address("127.0.0.1", 22556)
    val t : Transport = NettyTransport()
    val sr = Serializer()
    val tc = SingleThreadContext("srv-%d",sr)
    val acc = Account()
    val tlr = ThreadLocalRandom.current()
    val spread = Spread(me,
            "srv-${UUID(tlr.nextLong(),
                        tlr.nextLong())}", false)
    var phase: Phase = Phase.initPhase()

    fun run() {
        sr.register(Message::class.java)
        sr.register(Reply::class.java)
        tc.execute(this::handlers)
        while(readLine() == null);
        tc.close()
        t.close()
        println("I'm here")
    }

    private fun handlers() {
        println("Handling connection")
        spread.handler(Message::class.java, {
            sm : SpreadMessage,
            m : Message -> handler(m)
        }).handler(Reply::class.java, {
            sm : SpreadMessage,
            m : Reply -> handler(m)
        }).handler(NewMessage::class.java, {
            sm : SpreadMessage,
            m : NewMessage -> handler(m)
        })
    }

    private fun handler(m: Reply) {

    }

    private fun handler(m: NewMessage) {

    }

    private fun handler(m: Message) {

    }

}



