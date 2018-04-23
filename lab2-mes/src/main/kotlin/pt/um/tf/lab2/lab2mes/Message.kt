package pt.um.tf.lab2.lab2mes

import io.atomix.catalyst.buffer.BufferInput
import io.atomix.catalyst.buffer.BufferOutput
import io.atomix.catalyst.serializer.CatalystSerializable
import io.atomix.catalyst.serializer.Serializer

/**
 * Message with operation.
 */
data class Message(var op : Int = 0,
                   var mov : Int = 0)
    : CatalystSerializable {
    override fun writeObject(buffer: BufferOutput<*>?, serializer: Serializer?) {
        buffer?.writeInt(op)?.writeInt(mov);
    }

    override fun readObject(buffer: BufferInput<*>?, serializer: Serializer?) {
        op = buffer!!.readInt();
        mov = buffer.readInt();
    }
}
