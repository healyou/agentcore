package agentcore.database.dto

import agentcore.inputdata.ATableDesc
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.util.HashMap

/**
 * Created on 28.03.2017 19:56
 * @autor Nikita Gorodilov
 *
 * Класс, необходимый для чтения данных из бд
 * @param tableDesc данные о неизвестной заранее структуре таблицы
 */
class InputRowMapper(private val tableDesc: ATableDesc) : RowMapper<InputDataDto> {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int): InputDataDto {
        // данные, необходимые для создания объекта
        val paramType = HashMap<String, String>()
        val paramValue = HashMap<String, Any>()
        // значения столбцов таблицы пользователя
        val columns = tableDesc.columns

        // получаем тип данных параметров
        for (column in columns)
            paramType.put(column.columnName, column.columnType)

        // получаем значение считываемой строки таблицы
        for (key in paramType.keys) {
            when (paramType[key]) {
                "int" -> {
                    val columnName = key
                    paramValue.put(key, rs.getInt(columnName))
                }
                "String" -> {
                    val columnName = key
                    paramValue.put(key, rs.getString(columnName))
                }
                "double" -> {
                    val columnName = key
                    paramValue.put(key, rs.getDouble(columnName))
                }
                else -> {
                    System.exit(3);
                }
            }
        }

        // создание объекта строки таблицы бд
        return InputDataDto(paramType, paramValue)
    }
}