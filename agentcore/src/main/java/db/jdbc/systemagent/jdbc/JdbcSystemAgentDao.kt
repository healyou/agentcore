package db.jdbc.systemagent.jdbc

import db.base.AbstractDao
import db.base.toSqlite
import db.core.systemagent.SystemAgent
import db.jdbc.systemagent.SystemAgentDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcSystemAgentDao : AbstractDao(), SystemAgentDao {

    override fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent> {
        return jdbcTemplate.query(
                "select * from system_agent where is_deleted = ? and is_sendandget_messages = ?",
                SystemAgentRowMapper(),
                isDeleted.toSqlite(),
                isSendAndGetMessages.toSqlite()
        )
    }
}