package pt.um.tf.lab2.lab2mes

import java.io.Serializable

/**
 * NewMessage comes from source wishing to join the Clique.
 */
data class NewMessage(var origin : String = "") : Serializable
