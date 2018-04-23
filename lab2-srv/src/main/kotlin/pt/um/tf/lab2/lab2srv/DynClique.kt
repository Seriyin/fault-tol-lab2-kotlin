package pt.um.tf.lab2.lab2srv

import io.atomix.catalyst.concurrent.ThreadContext
import io.atomix.catalyst.transport.Address
import io.atomix.catalyst.transport.Connection
import io.atomix.catalyst.transport.Server
import io.atomix.catalyst.transport.Transport
import pt.um.tf.lab2.lab2mes.AddressMessage
import pt.um.tf.lab2.lab2mes.BeatMessage
import pt.um.tf.lab2.lab2mes.NewMessage
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger


/**
 * Dynamic Clique class manages leader election and connection upkeep.
 */
class DynClique(private val tc : ThreadContext,
                me: Address,
                private val transport: Transport) {
    companion object {
        val LOGGER = Logger.getLogger("Dynamic Clique")
    }
    private val conmap: MutableMap<Address, CachedConnection> = mutableMapOf()
    private var leader: Address = Address()
    private val server : Server = transport.server()

    init {
        server.listen(me, this::handleConnection)
    }

    private fun handleConnection(c : Connection) {
        c.handler<BeatMessage,Void>(BeatMessage::class.java,
                                    this::handleBeat)
         .handler<NewMessage,Void>(NewMessage::class.java,
                                   this::handleNew)
         .handler<AddressMessage,Void>(AddressMessage::class.java,
                                       this::handleAdr)
    }


    fun handleBeat(b : BeatMessage) {
        if (b.origin in conmap.keys) {
            when(b.status) {
                -1 -> Companion.LOGGER
                               .log(Level.WARNING,
                                    "${b.origin.host()}:${b.origin.port()} " +
                                    "is slow - ${LocalDateTime.now()}")
            }
        }
        else {
            tc.execute {
                transport.client()
                         .connect(b.origin)
                         .thenApply {
                            conmap[b.origin] = CachedConnection(it)
                         }
            }
        }
        
    }

    fun handleNew(n : NewMessage) {
        if (n.origin in conmap.keys) {
            conmap[n.origin]!!.send(AddressMessage(conmap.keys))
        }
        else {
            Companion.LOGGER.log(Level.WARNING, "New before beat")
        }
    }


    fun handleAdr(a : AddressMessage) {
        for (adr in a.keys) {
            if(adr !in conmap.keys) {
                tc.execute {
                    transport.client()
                            .connect(adr)
                            .thenApply {
                                conmap[adr] = CachedConnection(it)
                            }
                }
            }
        }
    }

}
