package database.dto

/**
 * Created on 28.03.2017 19:35
 * @autor Nikita Gorodilov
 */
class InputDataDto(paramType: HashMap<String, String>,
                   paramValue: HashMap<String, Any>) : DtoEntityImpl(paramType, paramValue) {
    companion object {
        val ID_COLUMN_NAME = "id"
        val ID_COLUMN_TYPE = "int"
    }

    init {
        if (!paramType.containsKey(ID_COLUMN_NAME))
            throw Exception()
    }
}