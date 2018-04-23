package pt.um.tf.lab2.lab2mes

import io.atomix.catalyst.buffer.BufferInput
import io.atomix.catalyst.buffer.BufferOutput
import io.atomix.catalyst.serializer.CatalystSerializable
import io.atomix.catalyst.serializer.Serializer
import io.atomix.catalyst.transport.Address

/**
 * Heartbeat message.
 */
data class BeatMessage(var status: Int = -1,
                       var origin : Address = Address()) : CatalystSerializable {

    override fun readObject(buffer: BufferInput<*>?,
                            serializer: Serializer?) {
        status = buffer!!.readInt()
        origin = serializer!!.readObject<Address>(buffer)
    }

    override fun writeObject(buffer: BufferOutput<*>?,
                             serializer: Serializer?) {
        buffer?.writeInt(status)
        serializer?.writeObject(origin)
    }

}
