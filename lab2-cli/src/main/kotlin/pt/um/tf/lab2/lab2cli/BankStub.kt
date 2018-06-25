package pt.um.tf.lab2.lab2cli

import io.atomix.catalyst.concurrent.ThreadContext
import io.atomix.catalyst.serializer.Serializer
import mu.KLogging
import pt.haslab.ekit.Spread
import pt.um.tf.lab2.lab2mes.Bank
import pt.um.tf.lab2.lab2mes.Message
import pt.um.tf.lab2.lab2mes.Reply
import spread.SpreadMessage
import java.util.concurrent.CompletableFuture

class BankStub(val me: String,
               val sp: Spread,
               val tc: ThreadContext) : Bank
{
    companion object : KLogging()

    private lateinit var comFMov : CompletableFuture<Boolean>
    private lateinit var comFBal : CompletableFuture<Long>
    private var i : Int = 0

    init {
        tc.execute {
            sp.open()
            sp.join("banks")
            sp.handler(Reply::class.java, this@BankStub::handle)
        }.get()
    }

    override fun movement(mov: Long): Boolean {
        comFMov = CompletableFuture()
        tc.execute {
            val spm = SpreadMessage()
            spm.setSafe()
            spm.addGroup("banks")
            sp.multicast(spm, Message(i,1, mov, me))
        }
        return comFMov.get()
    }

    override fun balance(): Long {
        comFBal = CompletableFuture()
        tc.execute {
            val spm = SpreadMessage()
            spm.setSafe()
            spm.addGroup("banks")
            sp.multicast(spm, Message(i,0, 0, me))
        }
        return comFBal.get()
    }


    private fun handle(sm : SpreadMessage, m: Reply) {
        logger.info { "Register reply" }
        when (m.op) {
            1 -> when {
                m.seq < i -> logger.info { "Repeat message" }
                m.seq == i -> {
                    comFMov.complete(m.denied)
                    i++
                }
                else -> logger.error { "Sequence is ahead of messaging" }
            }
            0 -> comFBal.complete(m.balance)
        }
    }


}
