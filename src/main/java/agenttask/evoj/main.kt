package agenttask.evoj

import net.sourceforge.evoj.core.DefaultPoolFactory
import net.sourceforge.evoj.handlers.DefaultHandler
import net.sourceforge.evoj.handlers.MultithreadedHandler

/**
 * Created on 22.05.2017 20:39
 * @autor Nikita Gorodilov
 */
class main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            var startTime = System.currentTimeMillis()
            var pf = DefaultPoolFactory()
            var pool = pf.createPool(800, Solution::class.java, null)
            val handler = DefaultHandler(Rating(), null, null, null)

            handler.iterate(pool, 5000)
            var solution = pool.bestSolution

            println("${solution.x} ${solution.y}")
            println("endTime 1 thread = ${System.currentTimeMillis() - startTime} мс")


            startTime = System.currentTimeMillis()
            pf = DefaultPoolFactory()
            pool = pf.createPool(800, Solution::class.java, null)
            val multiThreadingHandler = MultithreadedHandler(4, Rating(), null, null, null)

            multiThreadingHandler.iterate(pool, 5000)
            solution = pool.bestSolution
            multiThreadingHandler.shutdown()

            println("${solution.x} ${solution.y}")
            println("endTime multithreading = ${System.currentTimeMillis() - startTime} мс")
        }
    }
}