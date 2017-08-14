package agentcore.database.dao

import agentcore.database.dto.InputDataDto
import agentcore.database.dto.InputRowMapper
import agentcore.inputdata.ATableDesc
import agentcore.inputdata.InputDataTableDesc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class InputDataDao
/**
 * Осущ. чтение базы данных
 * @param jdbcTemplate чтение бд
 * @param tableDesc данные о таблице бд
 */
(private val jdbcTemplate: JdbcTemplate, private val tableDesc: InputDataTableDesc) : ABaseDao<InputDataDto>(), IInputDataDao<InputDataDto> {

    private val SELECT_FIRST_SQL: String = "select * from " + tableDesc.tableName + " order by ? limit 1"
    private val DELETE_SQL: String = "delete from " + tableDesc.tableName + " where id=?"

    @Transactional(readOnly = true)
    @Throws(SQLException::class)
    override fun getFirst(): InputDataDto? {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, arrayOf<Any>(ATableDesc.ID_COLUMN_NAME),
                InputRowMapper(tableDesc))
    }

    @Transactional
    @Throws(SQLException::class)
    override fun delete(entity: InputDataDto) {
        jdbcTemplate.update(DELETE_SQL, entity.getValueByColumnName(ATableDesc.ID_COLUMN_NAME))
    }
}
