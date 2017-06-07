package agentcore.database.dto

import agentcore.inputdata.ATableDesc
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.util.HashMap

/**
 * Created on 28.03.2017 19:56
 * @autor Nikita Gorodilov
 */
class LocalRowMapper(private val tableDesc: ATableDesc) : RowMapper<LocalDataDto> {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int): LocalDataDto {
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
            when (InputDataType.getByName(paramType[key]!!)) {
                InputDataType.STRING -> {
                    val columnName = key
                    paramValue.put(key, rs.getString(columnName))
                }
                InputDataType.INT -> {
                    val columnName = key
                    paramValue.put(key, rs.getInt(columnName))
                }
                InputDataType.DOUBLE -> {
                    val columnName = key
                    paramValue.put(key, rs.getDouble(columnName))
                }
                else -> {
                    throw UnsupportedOperationException("Не известный тип данных")
                }
            }
        }

        // создание объекта строки таблицы бд
        return LocalDataDto(paramType, paramValue)
    }
}