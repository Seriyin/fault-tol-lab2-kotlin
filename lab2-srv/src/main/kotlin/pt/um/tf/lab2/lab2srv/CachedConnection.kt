package pt.um.tf.lab2.lab2srv

import io.atomix.catalyst.serializer.CatalystSerializable
import io.atomix.catalyst.transport.Connection
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * CachedConnection detects invalidation of a connection.
 */
data class CachedConnection(private val connection : Connection,
                            private var invalidate : Int = 3) {
    /**
     * Auto invalidates if connection isn't renewed through heartbeats periodically.
     */
    fun oneRound() {
        invalidate = invalidate--
        if (isInvalid()) {
            invalidate()
        }
    }

    fun isInvalid() : Boolean {
        return invalidate >= 0
    }

    private fun invalidate() {
        connection.close()
    }

    /**
     * Renews connection if not already invalid.
     * @return whether the connection was already invalid.
     */
    fun renew() : Boolean {
        if(invalidate >= 0) {
            invalidate = 3
        }
        return isInvalid()
    }

    /**
     * Register handlers as long as they are catalyst serializable.
     */
    fun <T : CatalystSerializable> registerHandlers(handlers : List<ConnectionHandler<T>>) {
        for (h in handlers) {
            connection.handler<T,Void>(h.type,h.handler)
        }
    }

    fun <T : CatalystSerializable> send(item : T): CompletableFuture<Void> {
        return connection.send(item)
    }

    fun <T : CatalystSerializable, U : CatalystSerializable> sendAndReceive(item : T) : CompletableFuture<U> {
        return connection.sendAndReceive<T,U>(item)
    }

}
