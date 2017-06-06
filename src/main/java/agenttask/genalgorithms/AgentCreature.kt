package agenttask.genalgorithms

import genetics.simplecreature.SimpleCreature
import genetics.simplecreature.SimpleCrossFunction
import genetics.simplecreature.SimpleMutationFunction

/**
 * @author Nikita Gorodilov
 * @param inputData входные параметры задачи(размер равен bytes) (от 0 до 15 8 чисел максимум - 0-31 байт)
 * @param fromValue от кого идёт сигнал - текущий агент или другие агенты
 * @param bytes число используемых байт - 4 байта(1 число)
 * @param cross кроссинговер особи
 * @param mutation мутация особи
 */
class AgentCreature(private val inputData: IntArray,
                    private val fromValue: FromValue,
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
    }

    init {
        // Количество входых параметров не должно быть больше MAX_INPUT_DATA_SIZE
        // Количество байт должно быть кратно INPUT_DATA_BYTE_SIZE
        // Размер в байтах / INPUT_DATA_BYTE_SIZE должен быть равен числу вхордных параметров
        if (inputData.size > MAX_INPUT_DATA_SIZE ||
                (bytes + 1) % INPUT_DATA_BYTE_SIZE != 0 ||
                (bytes + 1) / INPUT_DATA_BYTE_SIZE != inputData.size)
            throw IllegalArgumentException(INPUT_DATA_EXCEPTION_TEXT)

        // входные параметры могут быть от 0 до MAX_INPUT_DATA_VALUE
        inputData.forEach {
            if (it < 0 || it > MAX_INPUT_DATA_VALUE)
                throw IllegalArgumentException(INPUT_DATA_EXCEPTION_TEXT)
        }
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
                UnsupportedOperationException()
                return 0.0
            }
        }
    }

    // todo дописать fit функции для агентов
    /**
     * Вычисляем для текущего агента
     */
    private fun fitCurrentAgent(): Double {
        val f = (super.q * super.q + super.q * 2 + 1).toDouble()
        return f
    }

    /**
     * Вычисляем для других агентов
     */
    private fun fitOtherAgent(): Double {
        val f = (super.q * super.q + super.q * 2 * 5 + 10).toDouble()
        return f
    }
}