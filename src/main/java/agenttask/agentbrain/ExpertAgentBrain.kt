package agenttask.agentbrain

import agentcore.agentfoundation.AAgentBrain
import agentcore.agentfoundation.AgentDatabaseImpl
import agentcore.database.dao.InputDataDao
import agentcore.database.dto.InputDataDto
import agentcore.database.dto.InputDataType
import net.sf.clipsrules.jni.FactAddressValue
import net.sf.clipsrules.jni.MultifieldValue

/**
 * @author Nikita Gorodilov
 */
class ExpertAgentBrain(mDao: InputDataDao,
                       mDb: AgentDatabaseImpl,
                       clipsFilePath: String? = null)
    : AAgentBrain(mDao, mDb) {

    private val clips = ClipsEnvironment()
    private val inputDataColumns = mDb.localDbTableDesc.columns

    companion object {
        private var CLIPS_FILE_PATH = "src\\main\\java\\agenttask\\initinputdb\\dataC\\clipsexpert.CLP"
    }

    init {
        if (clipsFilePath != null)
            CLIPS_FILE_PATH = clipsFilePath

        clips.clear()
        clips.load(CLIPS_FILE_PATH)
    }

    // todo определиться с выходными данными многоагентной системы

    override fun calculateAnswerValue(): String {
        clips.reset()

        val assertCommands = configureClipsAssertCommands()
        assertCommands.forEach {
            clips.eval(it)
        }

        clips.run()

        val evaluar = "(find-all-facts ((?f outputdata)) TRUE)"
        val bigVal = clips.eval(evaluar) as MultifieldValue
        val outputClipsValue = parseOutputClipsValue(bigVal)

        var ret = ""
        outputClipsValue.values.forEach {
            ret += it.toString()
        }

        return ret
    }

    /**
     * Запись входных данных в clips
     */
    private fun configureClipsAssertCommands(): ArrayList<String> {
        val inputDataArray = parseClipsInputData()
        val assertCommands = arrayListOf<String>()

        for (i in inputDataArray.indices) {
            val command = "(assert (inputdata (paramname " + inputDataColumns[i].columnName + ") " +
                    "(paramvalue " + inputDataArray[i] + ")))"
            assertCommands.add(command)
        }

        return assertCommands
    }

    private fun parseClipsInputData(): ArrayList<Int> {
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

    /**
     * Получаем выходные данные из clips
     */
    private fun parseOutputClipsValue(value: MultifieldValue): HashMap<String, Double> {
        val outputClipsData = hashMapOf<String, Double>()

        for (i in 0..value.size() - 1) {
            val fv = value.get(i) as FactAddressValue
            var paramName = ""
            var paramValue = 0.0
            try {
                paramName = fv.getFactSlot("paramname").toString()
                paramValue = fv.getFactSlot("paramvalue").toString().toDouble()
            } catch (e: Exception) {
                println(e.toString())
            } finally {
                outputClipsData.put(paramName, paramValue)
            }
        }

        return outputClipsData
    }
}