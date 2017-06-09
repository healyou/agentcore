package agenttask.agentbrain

import agentcore.agentfoundation.AAgentBrain
import agentcore.agentfoundation.AgentDatabaseImpl
import agentcore.database.dao.InputDataDao
import agentcore.database.dto.InputDataDto
import agentcore.database.dto.InputDataType
import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.neural.networks.BasicNetwork
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation
import org.encog.util.csv.CSVFormat
import org.encog.util.simple.TrainingSetUtil

/**
 * @author Nikita Gorodilov
 */
class NeuralAgentBrain(mDao: InputDataDao, mDb: AgentDatabaseImpl)
    : AAgentBrain(mDao, mDb) {

    private val network: BasicNetwork = BasicNetwork()
    private val inputLayerSize = mDb.localDbTableDesc.columns.size - 1
    private val outputLayerSize = 1
    private val trainError = 16.01

    init {
        network.addLayer(BasicLayer(null, true, inputLayerSize))
        network.addLayer(BasicLayer(ActivationSigmoid(), true, inputLayerSize * 3))
        network.addLayer(BasicLayer(ActivationSigmoid(), false, outputLayerSize))

        network.structure.finalizeStructure()
        network.reset()

        val currentDir = System.getProperty("user.dir")
        val inputFilePath = "$currentDir\\src\\main\\java\\agenttask\\initinputdb\\dataB\\neyraltraining.csv"

        val trainingSet = TrainingSetUtil.loadCSVTOMemory(
                CSVFormat.DECIMAL_POINT, inputFilePath, false, inputLayerSize, outputLayerSize)
        val train = ResilientPropagation(network,  trainingSet)

        var epoch = 1

        do {
            train.iteration()
            println("Epoch #" + epoch + " Error: " + train.error)
            epoch++;
        } while (train.error > trainError)
    }

    // todo точно определится с тем, по какой очерёдности должны идти входные параметры, тк это важно

    // todo написать тесты для NeuralAgentBrain

    override fun calculateAnswerValue(): String {
        mInputData ?: return ""

        val inputData = getInputData()
        val outputData = doubleArrayOf(0.0)

        network.compute(inputData, outputData)

        var out = ""
        try {
            out = outputData[0].toString()
        } catch (e: Exception) {
        } finally {
            return out
        }
    }

    private fun getInputData(): DoubleArray {
        val inputData = ArrayList<Double>()

        for (columnName in mInputData!!.columnNames) {
            if (columnName == InputDataDto.ID_COLUMN_NAME) continue

            val columnType = mInputData!!.getTypeByColumnName(columnName)
            when (InputDataType.getByName(columnType)) {
                InputDataType.STRING -> {
                    throw UnsupportedOperationException("Не известный тип данных")
                }
                InputDataType.DOUBLE -> {
                    val value = mInputData!!.getValueByColumnName(columnName)
                    inputData.add(value.toString().toDouble())
                }
                InputDataType.INT -> {
                    val value = mInputData!!.getValueByColumnName(columnName)
                    inputData.add(value.toString().toDouble())
                }
                else -> {
                    throw UnsupportedOperationException("Не известный тип данных")
                }
            }
        }

        if (inputData.size < inputLayerSize) {
            println("Неверное число входных параметров нейронной сети")
            for (i in 0..inputLayerSize - inputData.size - 1)
                inputData.add(0.0)
        }

        return inputData.toDoubleArray()
    }
}