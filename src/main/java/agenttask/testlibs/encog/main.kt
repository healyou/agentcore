package agenttask.testlibs.encog

import org.encog.Encog
import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.ml.data.MLDataSet
import org.encog.ml.data.basic.BasicMLDataSet
import org.encog.neural.networks.BasicNetwork
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation
import org.encog.util.csv.CSVFormat
import org.encog.util.simple.TrainingSetUtil

/**
 * Created by user on 26.05.2017.
 * @autor Nikita Gorodilov
 */
class main {

    companion object {

        val XOR_INPUT = arrayOf(
                doubleArrayOf(0.0, 0.0),
                doubleArrayOf(1.0, 0.0),
                doubleArrayOf(0.0, 1.0),
                doubleArrayOf(1.0, 1.0)
        )

        val XOR_IDEAL = arrayOf(
                doubleArrayOf(0.0),
                doubleArrayOf(1.0),
                doubleArrayOf(1.0),
                doubleArrayOf(0.0)
        )

        @JvmStatic
        fun main(args: Array<String>) {
            testLoadCsvData()
            defineTestNetwork()
        }

        private fun testLoadCsvData() {
            val currentDir = System.getProperty("user.dir")
            val inputFilePath = "$currentDir\\data\\neural\\xor.csv"

            val trainingSet = TrainingSetUtil.loadCSVTOMemory(
                    CSVFormat.DECIMAL_POINT, inputFilePath/*"C:\\Users\\user\\IdeaProjects\\agentcore\\data\\neural\\xor.csv"*/, false, 2, 1)

            trainingSet.forEach { println(it) }
        }

        private fun defineTestNetwork() {
            val network = BasicNetwork()

            network.addLayer(BasicLayer(null, true, 2))
            network.addLayer(BasicLayer(ActivationSigmoid(), true, 3))
            network.addLayer(BasicLayer(ActivationSigmoid(), false, 1))

            network.structure.finalizeStructure()
            network.reset()

            val trainingSet: MLDataSet = BasicMLDataSet(XOR_INPUT, XOR_IDEAL)
            val train = ResilientPropagation(network,  trainingSet)

            var epoch = 1

            do {
                train.iteration()
                println("Epoch #" + epoch + " Error: " + train.error)
                epoch++;
            } while (train.error > 0.01)

            // t e s t t h e n e u r al ne twork
            println("Neural Network Results: ")
            for (pair in trainingSet) {
                val output = network.compute(pair.input)
                println("" + pair.input.getData(0) + ", " + pair.input.getData(1) +
                        ", ideal=" + pair.ideal.getData(0) +
                        ", actual=" + output.getData(0))
            }

            Encog.getInstance().shutdown()
        }
    }
}