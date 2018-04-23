package pt.um.tf.lab2.lab2mes

interface Bank {
    fun movement(mov : Long) : Boolean
    fun balance() : Long
}
