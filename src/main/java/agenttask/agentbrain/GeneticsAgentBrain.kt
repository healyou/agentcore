package agenttask.agentbrain

import agentcore.agentfoundation.AAgentBrain
import agentcore.agentfoundation.AgentDatabaseImpl
import agentcore.database.dao.InputDataDao
import agentcore.database.dto.InputDataDto
import agentcore.database.dto.InputDataType
import genetics.choosing.ChoosingRandom
import genetics.selecting.SelectingMax
import genetics.simplecreature.SimpleCrossOnePoint
import genetics.simplecreature.SimpleMutationOneBit
import genetics.stopping.StoppingIterations

/**
 * @author Nikita Gorodilov
 */
class GeneticsAgentBrain(mDao: InputDataDao, mDb: AgentDatabaseImpl)
    : AAgentBrain(mDao, mDb) {

    val iterations = 100
    val size = 100
    val chooses = 0.4
    val mutates = 0.02

    val clipsEnvironment = ClipsEnvironment()
    val choosing = ChoosingRandom()
    val selecting = SelectingMax()
    val stopping = StoppingIterations(iterations)
    val cross = SimpleCrossOnePoint()
    val mutation = SimpleMutationOneBit()

    // todo написать тесты для агента этого мозга агента
    // todo разобраться с выходным типом данных агента, что посылать и как считывать

    override fun calculateAnswerValue(): String {
        mInputData ?: return ""

        val population = configurePopulation()

        population.run()
        val outCreature = population.answerCreature

        return outCreature.fit().toString()
    }

    private fun configurePopulation(): AgentPopulation<AgentCreature> {
        val inputData = getInputData()
        val inputDataParamName = getInputDataParamName()

        if (inputData.size != inputDataParamName.size)
            throw IllegalArgumentException("Неверный формат данных")

        val agentCreature = AgentCreature(inputData, inputDataParamName, AgentCreature.FromValue.CURRENT_AGENT, clipsEnvironment, inputData.size * 4 - 1, cross, mutation)
        val population = AgentPopulation(size, chooses, mutates, agentCreature, choosing, selecting, stopping)

        return population
    }

    private fun getInputData(): ArrayList<Int> {
        val inputData = arrayListOf<Int>()

        for (columnName in mInputData!!.columnNames) {
            if (columnName == InputDataDto.ID_COLUMN_NAME) continue

            val columnType = mInputData!!.getTypeByColumnName(columnName)
            when (InputDataType.getByName(columnType)) {
                InputDataType.STRING -> {
                    throw UnsupportedOperationException("Не известный тип данных")
                }
                InputDataType.DOUBLE -> {
                    val value = mInputData!!.getValueByColumnName(columnName)
                    inputData.add(value.toString().toDouble().toInt())
                }
                InputDataType.INT -> {
                    val value = mInputData!!.getValueByColumnName(columnName)
                    inputData.add(value.toString().toInt())
                }
                else -> {
                    throw UnsupportedOperationException("Не известный тип данных")
                }
            }
        }

        return inputData
    }

    private fun getInputDataParamName(): ArrayList<String> {
        val inputDataParamName = arrayListOf<String>()

        mInputData!!.columnNames.forEach {
            if (it != InputDataDto.ID_COLUMN_NAME)
                inputDataParamName.add(it)
        }

        return inputDataParamName
    }
}