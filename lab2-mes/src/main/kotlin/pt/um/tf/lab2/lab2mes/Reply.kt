package pt.um.tf.lab2.lab2mes

import io.atomix.catalyst.buffer.BufferInput
import io.atomix.catalyst.buffer.BufferOutput
import io.atomix.catalyst.serializer.CatalystSerializable
import io.atomix.catalyst.serializer.Serializer

/**
 * Reply to account operation.
 */
data class Reply(var op : Int = 0,
                 var denied : Boolean = false,
                 var balance : Int = 0)
    : CatalystSerializable {

    override fun readObject(buffer: BufferInput<*>?,
                            serializer: Serializer?) {
        op = buffer!!.readInt()
        denied = buffer.readBoolean()
        balance = buffer.readInt()
    }

    override fun writeObject(buffer: BufferOutput<*>?,
                             serializer: Serializer?) {
        buffer?.writeInt(op)
                ?.writeBoolean(denied)
                ?.writeInt(balance)
    }
}
