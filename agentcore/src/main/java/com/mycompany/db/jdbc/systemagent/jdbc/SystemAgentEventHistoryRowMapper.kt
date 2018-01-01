package com.mycompany.db.jdbc.systemagent.jdbc

import com.mycompany.db.base.AbstractRowMapper
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class SystemAgentEventHistoryRowMapper : AbstractRowMapper<SystemAgentEventHistory>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, index: Int): SystemAgentEventHistory {
        val history = SystemAgentEventHistory(
                getLong(rs, "agent_id"),
                getString(rs, "message")
        )
        history.id = getLong(rs, "id")
        history.createDate = getDate(rs, "create_date")
        
        return history
    }
}