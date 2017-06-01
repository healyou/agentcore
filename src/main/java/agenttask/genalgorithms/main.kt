package agenttask.genalgorithms

import genetics.choosing.ChoosingRandom
import genetics.population.Population
import genetics.selecting.SelectingMax
import genetics.simplecreature.SimpleCrossOnePoint
import genetics.simplecreature.SimpleMutationOneBit
import genetics.stopping.StoppingIterations

/**
 * @author Nikita Gorodilov
 */
class main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            /*n - Число особей в популяции.
           chooses - Доля скрещиваемых особей.
           mutates - Доля мутирующих особей.
           c - Особь-прототип, которая содержит набор данных (генотип), а также операции работы с данными: скрещивание и мутация.
           ch - Объект-наследник класса Choosing, в котором реализована функция выбора особей для скрещивания crossing.
           sel - Объект-наследник класса Selecting, в котором реализована функция отбора особей select.
           st - Объект-наследник класса Stopping, в котором реализована функция проверки условия остановки алгоритма isEnding.*/
            val iterations = 10000

            val size = 1000
            val chooses = 0.4
            val mutates = 0.02
            val choosing = ChoosingRandom()
            val selecting = SelectingMax()
            val stopping = StoppingIterations(iterations)
            val cross = SimpleCrossOnePoint()
            val mutation = SimpleMutationOneBit()
            val creature = MyCreature(31, cross, mutation);

            val population = Population(size, chooses, mutates, creature, choosing, selecting, stopping)
            population.run()
            val outCreature = population.answerCreature as MyCreature
            println(outCreature.fit())
        }
    }
}
