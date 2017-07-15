package agentcore.database.dto

/**
 * Базовый класс объекта локальной базы данных
 * Нужен для наследования реальногообъекта базы данных
 * и объекта сообщения по сети(одни и теже данные, но id нужен только здесь)
 *
 * @author Nikita Gorodilov
 */
abstract class ALocalDataDto(paramType: HashMap<String, String>,
                             paramValue: HashMap<String, Any>)
    : ConfigureEntityImpl(paramType, paramValue) {

    companion object {
        val ID_COLUMN_NAME = "id"
        val ID_COLUMN_TYPE = "int"
        val ANSWER_COLUMN_NAME = "answer"
        val ANSWER_COLUMN_TYPE = "String"
        val COLLECTIVEANSWER_COLUMN_NAME = "collectiveanswer"
        val COLLECTIVEANSWER_COLUMN_TYPE = "String"
    }

    init {
        if (!paramType.containsKey(ID_COLUMN_NAME) ||
                !paramType.containsKey(ANSWER_COLUMN_NAME) ||
                !paramType.containsKey(COLLECTIVEANSWER_COLUMN_NAME))
            throw Exception()
    }

    fun setAnswerValue(value: Any) {
        paramValue.put(ANSWER_COLUMN_NAME, value)
    }

    fun setColAnswerValue(value: Any) {
        paramValue.put(COLLECTIVEANSWER_COLUMN_NAME, value)
    }
}