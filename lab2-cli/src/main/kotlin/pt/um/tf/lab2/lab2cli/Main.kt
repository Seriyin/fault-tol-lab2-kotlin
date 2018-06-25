package pt.um.tf.lab2.lab2cli

import java.util.concurrent.*
import kotlin.system.exitProcess


fun main(args : Array<String>) {
    val bf = BankFactory()
    val r = (2..ForkJoinPool.getCommonPoolParallelism()).random()
    var balance : Long = 0
    val q : BlockingQueue<Long> = ArrayBlockingQueue<Long>(r)
    repeat(r) {
        val sp = Spammer(it, bf, q)
        ForkJoinPool.commonPool().execute(sp::execute)
    }
    repeat(r) {
        balance += q.take()
    }
    val b = bf.newBank()
    println("Got $balance, Expected ${b.balance()}")
    bf.closeBanks()
    println("I'm done")
    exitProcess(0)
}

fun ClosedRange<Int>.random() = ThreadLocalRandom.current().nextInt(this.start, this.endInclusive)

