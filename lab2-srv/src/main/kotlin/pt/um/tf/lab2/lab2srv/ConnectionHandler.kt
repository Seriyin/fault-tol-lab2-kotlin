package pt.um.tf.lab2.lab2srv

import io.atomix.catalyst.transport.Connection
import java.util.function.Consumer

/**
 * A connection handler for catalyst connections.
 */
data class ConnectionHandler<T>(val type : Class<T>,
                                val handler: Consumer<T>)
