package agenttask.testlibs.genalgorithms

import agenttask.agentbrain.AgentCreature
import agenttask.agentbrain.AgentPopulation
import agenttask.agentbrain.ClipsEnvironment
import genetics.choosing.ChoosingRandom
import genetics.population.Population
import genetics.selecting.SelectingMax
import genetics.simplecreature.SimpleCrossOnePoint
import genetics.simplecreature.SimpleMutationOneBit
import genetics.stopping.StoppingIterations
import net.sf.clipsrules.jni.Environment
import net.sf.clipsrules.jni.FactAddressValue
import net.sf.clipsrules.jni.MultifieldValue

/**
 * @author Nikita Gorodilov
 */
class main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val startTime = System.currentTimeMillis()
            testGeneticsWithClipsEnvironment()
            val endTime = System.currentTimeMillis() - startTime
            println(endTime)

//            testClipsForAgentC()

//            testLoadClips()
//            testAgentGenetics()
//            testGenetics()
        }

        private fun testGeneticsWithClipsEnvironment() {
            val iterations = 10
            val size = 1000
            val chooses = 0.4
            val mutates = 0.02
            val choosing = ChoosingRandom()
            val selecting = SelectingMax()
            val stopping = StoppingIterations(iterations)
            val cross = SimpleCrossOnePoint()
            val mutation = SimpleMutationOneBit()
            val inputData = arrayListOf<Int>(0, 1, 2, 3)
            val inputDataParamName = arrayListOf<String>("occupancyC", "conditionC", "condition_d3", "occupancy_d3")

            val agentCreature = AgentCreature(inputData, inputDataParamName, AgentCreature.FromValue.CURRENT_AGENT, ClipsEnvironment(), inputData.size * 4 - 1, cross, mutation)
            println("fit=" + agentCreature.fit())

            val creature = AgentCreature(inputData, inputDataParamName, AgentCreature.FromValue.OTHER_AGENT, ClipsEnvironment(), inputData.size * 4 - 1, cross, mutation)

            val population = AgentPopulation(size, chooses, mutates, agentCreature, choosing, selecting, stopping)
            population.run()

            val outCreature = population.answerCreature
            println(outCreature.fit())
        }

        private fun testClipsForAgentC() {
            val clips = Environment()
            clips.load("src\\main\\java\\agenttask\\initinputdb\\dataC\\clipsfit.CLP")
            clips.reset()

            val assertCommands = arrayListOf<String>(
                    "(assert (inputdata (paramname occupancyC) (paramvalue 1)))",
                    "(assert (inputdata (paramname conditionC) (paramvalue 2)))",
                    "(assert (inputdata (paramname condition_d3) (paramvalue 3)))",
                    "(assert (inputdata (paramname occupancy_d3) (paramvalue 4)))"
            )
            assertCommands.forEach {
                clips.eval(it)
            }

            clips.run()

            var evaluar = "(find-all-facts ((?f inputdata)) TRUE)"//"(facts)";
            var value = clips.eval(evaluar)
            if (value.javaClass == MultifieldValue::class.java) {
                //FactAddressValue value = (FactAddressValue) ((MultifieldValue) clips.eval(evaluar)).get(0);
                val bigVal = clips.eval(evaluar) as MultifieldValue
                for (i in 0..bigVal.size() - 1) {
                    val fv = bigVal.get(i) as FactAddressValue
                    try {
                        //PrimitiveValue str = fv.getFactSlot("color");
                        println("inputdata-" + fv.getFactSlot("paramname").toString())
                        println("inputdata-" + fv.getFactSlot("paramvalue").toString())
                    } catch (e: Exception) {
                        println(e.toString())
                    }

                }
            } else
                println(value)

            println()

            evaluar = "(find-all-facts ((?f outputdata)) TRUE)"//"(facts)";
            value = clips.eval(evaluar)
            if (value.javaClass == MultifieldValue::class.java) {
                //FactAddressValue value = (FactAddressValue) ((MultifieldValue) clips.eval(evaluar)).get(0);
                val bigVal = clips.eval(evaluar) as MultifieldValue
                for (i in 0..bigVal.size() - 1) {
                    val fv = bigVal.get(i) as FactAddressValue
                    try {
                        //PrimitiveValue str = fv.getFactSlot("color");
                        println("outputdata-" + fv.getFactSlot("paramname").toString())
                        println("outputdata-" + fv.getFactSlot("paramvalue").toString())
                    } catch (e: Exception) {
                        println(e.toString())
                    }

                }
            } else
                println(value)

            clips.reset()
            clips.destroy()
        }

        private fun testLoadClips() {
            val currentDir = System.getProperty("user.dir")
            val inputFilePath = "$currentDir\\libs\\CLIPSJNI.dll"
            System.load(inputFilePath)

            val clips = Environment()
            clips.load("data/clips/myclips.CLP")
            clips.reset()
            clips.run()
            var assertCommand = "(assert (addpersons add1))"
            //clips.assertString(assertCommand);
            clips.eval(assertCommand)
            assertCommand = "(assert (addpersons add))"
            clips.eval(assertCommand)

            assertCommand = "(assert (testdef (testcolor c) (testname n)))"
            clips.eval(assertCommand)
            clips.run()

            /*
            evalStr = "(find-fact ((?f technique-employed)) " +
                    "(eq ?f:priority " + i + "))";
            mv = (MultifieldValue) clips.eval(evalStr);
            */

            //clips.clear();
            assertCommand = "(rules)"
            var value = clips.eval(assertCommand)
            println(value)

            var evaluar = "(find-all-facts ((?f addpersons)) TRUE)"//"(facts)";
            value = clips.eval(evaluar)
            if (value.javaClass == MultifieldValue::class.java) {
                //FactAddressValue value = (FactAddressValue) ((MultifieldValue) clips.eval(evaluar)).get(0);
                val bigVal = clips.eval(evaluar) as MultifieldValue
                for (i in 0..bigVal.size() - 1) {
                    val fv = bigVal.get(i) as FactAddressValue
                    try {
                        //fv.
                        println(fv.getFactSlot("").toString())
                    } catch (e: Exception) {
                        println(e.toString())
                    }

                    println(bigVal.get(i))
                }
            } else
            //PrimitiveValue value = clips.eval(evaluar);
                println(value)

            evaluar = "(find-all-facts ((?f testdef)) TRUE)"//"(facts)";
            value = clips.eval(evaluar)
            if (value.javaClass == MultifieldValue::class.java) {
                //FactAddressValue value = (FactAddressValue) ((MultifieldValue) clips.eval(evaluar)).get(0);
                val bigVal = clips.eval(evaluar) as MultifieldValue
                for (i in 0..bigVal.size() - 1) {
                    val fv = bigVal.get(i) as FactAddressValue
                    try {
                        //PrimitiveValue str = fv.getFactSlot("color");
                        println(fv.getFactSlot("testcolor").toString())
                        println(fv.getFactSlot("testname").toString())
                    } catch (e: Exception) {
                        println(e.toString())
                    }

                }
            } else
            //PrimitiveValue value = clips.eval(evaluar);
            println(value)

            clips.reset()
            clips.destroy()
        }

        private fun testAgentGenetics() {
            val iterations = 100
            val size = 1000
            val chooses = 0.4
            val mutates = 0.02
            val choosing = ChoosingRandom()
            val selecting = SelectingMax()
            val stopping = StoppingIterations(iterations)
            val cross = SimpleCrossOnePoint()
            val mutation = SimpleMutationOneBit()
            val inputData = arrayListOf<Int>(0, 1, 2, 3, 4, 5)
            val inputDataParamName = arrayListOf<String>("0", "1", "2", "3", "4", "5")
            val creature = AgentCreature(inputData, inputDataParamName, AgentCreature.FromValue.OTHER_AGENT, ClipsEnvironment(), inputData.size * 4 - 1, cross, mutation)

            val population = Population(size, chooses, mutates, creature, choosing, selecting, stopping)
            population.run()

            val outCreature = population.answerCreature as AgentCreature
            println(outCreature.fit())
        }

        private fun testGenetics() {
            /*n - Число особей в популяции.
            chooses - Доля скрещиваемых особей.
            mutates - Доля мутирующих особей.
            c - Особь-прототип, которая содержит набор данных (генотип), а также операции работы с данными: скрещивание и мутация.
            ch - Объект-наследник класса Choosing, в котором реализована функция выбора особей для скрещивания crossing.
            sel - Объект-наследник класса Selecting, в котором реализована функция отбора особей select.
            st - Объект-наследник класса Stopping, в котором реализована функция проверки условия остановки алгоритма isEnding.*/
            val iterations = 100

            val size = 1000
            val chooses = 0.4
            val mutates = 0.02
            val choosing = ChoosingRandom()
            val selecting = SelectingMax()
            val stopping = StoppingIterations(iterations)
            val cross = SimpleCrossOnePoint()
            val mutation = SimpleMutationOneBit()
            val creature = MyCreature(31, cross, mutation)

//            var temp = creature.get()
//            println("${creature.getbytes()}")
//            println("$temp")
//            println("${creature.generate()}")
//            temp = creature.get()
//            println("${creature.generate()}")
//            temp = creature.get()
//            println("${creature.get()}")

            val population = Population(size, chooses, mutates, creature, choosing, selecting, stopping)
            population.run()
            val outCreature = population.answerCreature as MyCreature
            println(outCreature.fit())
        }
    }
}