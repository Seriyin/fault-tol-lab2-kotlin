package pt.um.tf.lab2.lab2cli

import io.atomix.catalyst.concurrent.SingleThreadContext
import io.atomix.catalyst.concurrent.ThreadContext
import io.atomix.catalyst.serializer.Serializer
import pt.haslab.ekit.Spread
import pt.um.tf.lab2.lab2mes.Bank
import pt.um.tf.lab2.lab2mes.Message
import pt.um.tf.lab2.lab2mes.Reply
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class BankFactory {
    private val tlr = ThreadLocalRandom.current()
    private val l = arrayListOf<Pair<ThreadContext,Spread>>()

    fun newBank() : Bank {
        val sr = Serializer()
        val me = UUID.randomUUID().toString()
        val sp = Spread("cli-$me",false)
        sr.register(Message::class.java)
        sr.register(Reply::class.java)
        val tc : ThreadContext = SingleThreadContext("cli-%d", sr)
        l.add(Pair(tc,sp))
        return BankStub(me, sp, tc)
    }

    fun closeBanks() {
        l.forEach{ (a, b) ->
            a.close()
            b.close()
        }
        l.clear()
    }

}
