package agentcore.database.dto

/**
 * Created on 28.03.2017 19:35
 * @author Nikita Gorodilov
 */
class InputDataDto(paramType: HashMap<String, String>,
                   paramValue: HashMap<String, Any>)
    : ConfigureEntityImpl(paramType, paramValue), ABaseDtoEntity {

    companion object {
        val ID_COLUMN_NAME = "id"
        val ID_COLUMN_TYPE = "int"
    }

    init {
        if (!paramType.containsKey(ID_COLUMN_NAME))
            throw Exception()
    }

    override fun getId(): Long? {
        try {
            return paramValue[ALocalDataDto.ID_COLUMN_NAME] as Long?

        } catch (e : java.lang.Exception) {
            return null
        }
    }
}