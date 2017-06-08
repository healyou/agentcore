package agenttask.agentbrain

import genetics.simplecreature.SimpleCreature
import genetics.simplecreature.SimpleCrossFunction
import genetics.simplecreature.SimpleMutationFunction
import net.sf.clipsrules.jni.FactAddressValue
import net.sf.clipsrules.jni.MultifieldValue

/**
 * @author Nikita Gorodilov
 * @param inputData входные параметры задачи(размер равен bytes) (от 0 до 15 8 чисел максимум - 0-31 байт)
 * @param inputDataParamName имена переменных в базе данных - для поиска фактов в clips
 * @param fromValue от кого идёт сигнал - текущий агент или другие агенты
 * @param clipsEnvironment загрузка clips
 * @param bytes число используемых байт - 4 байта(1 число)
 * @param cross кроссинговер особи
 * @param mutation мутация особи
 */
open class AgentCreature(private val inputData: ArrayList<Int>,
                         private val inputDataParamName: ArrayList<String>,
                         private val fromValue: AgentCreature.FromValue,
                         private val clips: ClipsEnvironment,
                         bytes: Int,
                         cross: SimpleCrossFunction,
                         mutation: SimpleMutationFunction):
        SimpleCreature(bytes, cross, mutation) {

    enum class FromValue {
        CURRENT_AGENT,
        OTHER_AGENT
    }

    companion object {
        private val MAX_INPUT_DATA_VALUE = 15
        private val MAX_INPUT_DATA_SIZE = 8
        private val INPUT_DATA_BYTE_SIZE = 4

        private val INPUT_DATA_EXCEPTION_TEXT = "Неверный формат входных данных"
        private val CLIPS_FILE_PATH = "src\\main\\java\\agenttask\\initinputdb\\dataC\\clips.CLP"
    }

    init {
        // Количество входых параметров не должно быть больше MAX_INPUT_DATA_SIZE
        // Количество байт должно быть кратно INPUT_DATA_BYTE_SIZE
        // Размер в байтах / INPUT_DATA_BYTE_SIZE должен быть равен числу вхордных параметров
        if (inputData.size > MAX_INPUT_DATA_SIZE ||
                inputDataParamName.size != inputData.size ||
                (bytes + 1) % INPUT_DATA_BYTE_SIZE != 0 ||
                (bytes + 1) / INPUT_DATA_BYTE_SIZE != inputData.size)
            throw IllegalArgumentException(INPUT_DATA_EXCEPTION_TEXT)

        // входные параметры могут быть от 0 до MAX_INPUT_DATA_VALUE
        inputData.forEach {
            if (it < 0 || it > MAX_INPUT_DATA_VALUE)
                throw IllegalArgumentException(INPUT_DATA_EXCEPTION_TEXT)
        }

        clips.clear()
        clips.load(CLIPS_FILE_PATH)
    }

    override fun fit(): Double {
        when (fromValue) {
            FromValue.CURRENT_AGENT -> {
                return fitCurrentAgent()
            }
            FromValue.OTHER_AGENT -> {
                return fitOtherAgent()
            }
            else -> {
                throw UnsupportedOperationException()
            }
        }
    }

    // todo clips файл допилить для агента
    /**
     * Вычисляем для текущего агента
     */
    private fun fitCurrentAgent(): Double {
        clips.reset()

        val assertCommands = configureClipsAssertCommands()
        assertCommands.forEach {
            clips.eval(it)
        }

        clips.run()

        val evaluar = "(find-all-facts ((?f outputdata)) TRUE)"
        val bigVal = clips.eval(evaluar) as MultifieldValue
        val outputClipsValue = parseOutputClipsValue(bigVal)

        return outputClipsValue
    }

    // todo дописать fit функции для other агентов
    /**
     * Вычисляем для других агентов
     */
    private fun fitOtherAgent(): Double {
        val f = (super.q * super.q + super.q * 2 * 5 + 10).toDouble()
        return f
    }

    /**
     * Получаем выходные данные из clips
     */
    private fun parseOutputClipsValue(value: MultifieldValue): Double {
        val outputClipsData = arrayListOf<Double>()

        for (i in 0..value.size() - 1) {
            val fv = value.get(i) as FactAddressValue
            var paramValue = 0.0
            try {
                paramValue = fv.getFactSlot("paramvalue").toString().toDouble()
            } catch (e: Exception) {
                println(e.toString())
            } finally {
                outputClipsData.add(paramValue)
            }
        }

        return outputClipsData[0]
    }

    /**
     * Запись входных данных в clips
     */
    private fun configureClipsAssertCommands(): ArrayList<String> {
        val inputDataArray = parseClipsInputData()
        val assertCommands = arrayListOf<String>()

        for (i in inputDataArray.indices) {
            val command = "(assert (inputdata (paramname " + inputDataParamName[i] + ") " +
                    "(paramvalue " + inputDataArray[i] + ")))"
            assertCommands.add(command)
        }

        return assertCommands
    }

    /**
     * Из int 32 байта получаем наши inputData.size чисел по INPUT_DATA_BYTE_SIZE байт
     */
    private fun parseClipsInputData(): ArrayList<Int> {
        var x = get()
        val inputDataArray = arrayListOf<Int>()

        // сюда положим наши числа по 4 бита
        val byteDataArray = Array<Int>(inputData.size) { 0 }
        val mask = 1
        for (i in inputData.indices) {
            var byte = 0
            for (j in 0..INPUT_DATA_BYTE_SIZE - 1) {
                byte = byte.shr(1)
                if (x.and(mask) == 1)
                    byte = byte.or(mask.shl(INPUT_DATA_BYTE_SIZE - 1))
                x = x.shr(1)
            }

            byteDataArray[i] = byte
        }

        for (i in 0..byteDataArray.size - 1)
            inputDataArray.add(byteDataArray[i])

        return inputDataArray
    }
}