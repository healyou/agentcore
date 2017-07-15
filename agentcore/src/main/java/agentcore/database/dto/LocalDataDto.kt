package agentcore.database.dto

import java.lang.Exception

/**
 * Объект локальной базы данных агента
 *
 * @author Nikita Gorodilov
 */
class LocalDataDto(paramType: HashMap<String, String>,
                   paramValue: HashMap<String, Any>)
    : ALocalDataDto(paramType, paramValue), ABaseDtoEntity {

    companion object {

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

        /**
         * todo ниже
         * Нужно учитывать, что id объекта MessageLocalDataDto должен быть родным
         * для данного агента
         */
        fun valueOf(messageDto: MessageLocalDataDto): LocalDataDto {
            return LocalDataDto(messageDto.paramType, messageDto.paramValue)
        }
    }

    override fun getId(): Long? {
        try {
            return paramValue[ID_COLUMN_NAME] as Long?

        } catch (e : Exception) {
            return null
        }
    }
}