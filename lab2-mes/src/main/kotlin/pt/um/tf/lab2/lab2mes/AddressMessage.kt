package pt.um.tf.lab2.lab2mes

import io.atomix.catalyst.buffer.BufferInput
import io.atomix.catalyst.buffer.BufferOutput
import io.atomix.catalyst.serializer.CatalystSerializable
import io.atomix.catalyst.serializer.Serializer
import io.atomix.catalyst.transport.Address


/**
 * Message with known addresses in dynamic clique.
 */
data class AddressMessage(var keys : MutableSet<Address> = mutableSetOf(),
                          var leader: Address = Address())
    : CatalystSerializable {
    override fun writeObject(buffer: BufferOutput<*>?, serializer: Serializer?) {
        serializer?.writeObject(keys)
        serializer?.writeObject(leader)
    }

    override fun readObject(buffer: BufferInput<*>?, serializer: Serializer?) {
        keys = serializer!!.readObject<MutableSet<Address>>(buffer)
        leader = serializer!!.readObject(buffer)
    }

}
