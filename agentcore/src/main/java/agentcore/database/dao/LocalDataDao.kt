package agentcore.database.dao

import agentcore.database.dto.ConfigureEntityImpl
import agentcore.database.dto.InputDataType
import agentcore.database.dto.LocalDataDto
import agentcore.database.dto.LocalRowMapper
import agentcore.inputdata.LocalDataTableDesc
import agentcore.inputdata.ATableDesc
import agentcore.utils.Codable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class LocalDataDao
/**
 * Осущ. чтение базы данных
 * @param jdbcTemplate чтение бд
 * *
 * @param localdbTableDesc данные о таблице бд
 */
(private val jdbcTemplate: JdbcTemplate, private val tableDesc: LocalDataTableDesc) : ABaseDao<LocalDataDto>(), ILocalDataDao<LocalDataDto> {

    private val SELECT_BYID_SQL: String = "select * from " + tableDesc.tableName + " where " +
            ATableDesc.ID_COLUMN_NAME + " = ?"

    @Transactional(readOnly = true)
    @Throws(SQLException::class)
    override fun get(id: Int): LocalDataDto {
        return jdbcTemplate.queryForObject(
                SELECT_BYID_SQL, arrayOf<Any>(id),
                LocalRowMapper(tableDesc))
    }

    @Transactional
    @Throws(SQLException::class)
    override fun create(entity: LocalDataDto) {
        val INSERT_SQL = configureInsertSql(entity, tableDesc)
        jdbcTemplate.update(INSERT_SQL)
    }

    @Transactional
    @Throws(SQLException::class)
    override fun update(entity: LocalDataDto) {
        val UPDATE_SQL = configureUpdateSql(entity, tableDesc)
        jdbcTemplate.update(UPDATE_SQL)
    }

    private fun configureUpdateSql(entity: ConfigureEntityImpl, tableDesc: LocalDataTableDesc): String {
        val updateSql = StringBuilder()

        updateSql.append("update ").append(tableDesc.tableName).append(" set ")
        for (columnName in entity.getColumnNames())
            updateSql.append(columnName).append(" = ").append(entity.getValueByColumnName(columnName)).append(",")
        // замена последней запятой
        updateSql.replace(updateSql.length - 1, updateSql.length, " where " +
                ATableDesc.ID_COLUMN_NAME + " = " + entity.getValueByColumnName(ATableDesc.ID_COLUMN_NAME))

        return updateSql.toString()
    }

    /**
     * запись создаётся с тем id, который будет указан в entity
     */
    private fun configureInsertSql(entity: ConfigureEntityImpl, tableDesc: LocalDataTableDesc): String {
        val updateSql = StringBuilder()

        updateSql.append("insert into ").append(tableDesc.tableName).append(" (")
        for (columnName in entity.getColumnNames())
        //if (!columnName.equals(ATableDesc.ID_COLUMN_NAME))
            updateSql.append(columnName).append(",")
        // замена последней запятой
        updateSql.replace(updateSql.length - 1, updateSql.length, ") values (")

        for (columnName in entity.getColumnNames()) {
            val typeName = entity.getTypeByColumnName(columnName) ?: throw NullPointerException("typeName is null")
            when (Codable.find(InputDataType::class.java, typeName)) {
                InputDataType.STRING -> {
                    updateSql.append('\'')
                    updateSql.append(entity.getValueByColumnName(columnName))
                    updateSql.append("',")
                }
                InputDataType.INT -> {
                    updateSql.append(entity.getValueByColumnName(columnName))
                    updateSql.append(',')
                }
                InputDataType.DOUBLE -> {
                    updateSql.append(entity.getValueByColumnName(columnName))
                    updateSql.append(',')
                }
            }
        }

        // замена последней запятой
        updateSql.replace(updateSql.length - 1, updateSql.length, ")")

        return updateSql.toString()
    }
}
