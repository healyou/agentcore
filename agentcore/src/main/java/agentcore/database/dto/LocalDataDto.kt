package agentcore.database.dto

/**
 * Created on 28.03.2017 19:35
 * @author Nikita Gorodilov
 */
class LocalDataDto(paramType: HashMap<String, String>,
                   paramValue: HashMap<String, Any>) : ConfigureEntityImpl(paramType, paramValue) {
    companion object {
        val ID_COLUMN_NAME = "id"
        val ID_COLUMN_TYPE = "int"
        val ANSWER_COLUMN_NAME = "answer"
        val ANSWER_COLUMN_TYPE = "String"
        val COLLECTIVEANSWER_COLUMN_NAME = "collectiveanswer"
        val COLLECTIVEANSWER_COLUMN_TYPE = "String"

        fun valueOf(inputData: InputDataDto): LocalDataDto {
            val paramType = HashMap<String, String>()
            val paramValue = HashMap<String, Any>()

            for (column in inputData.getColumnNames()) {
                paramType.put(column, inputData.getTypeByColumnName(column) ?: throw RuntimeException(""))
                paramValue.put(column, inputData.getValueByColumnName(column) ?: throw RuntimeException(""))
            }

            paramType.put(ANSWER_COLUMN_NAME, ANSWER_COLUMN_TYPE)
            paramValue.put(ANSWER_COLUMN_NAME, "")
            paramType.put(COLLECTIVEANSWER_COLUMN_NAME, COLLECTIVEANSWER_COLUMN_TYPE)
            paramValue.put(COLLECTIVEANSWER_COLUMN_NAME, "")

            return LocalDataDto(paramType, paramValue)
        }
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