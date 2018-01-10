package com.mycompany.db.jdbc.systemagent.jdbc

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import com.mycompany.db.jdbc.systemagent.SystemAgentEventHistoryDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
class JdbcSystemAgentEventHistoryDao : AbstractDao(), SystemAgentEventHistoryDao {

    override fun create(history: SystemAgentEventHistory): Long {
        jdbcTemplate.update(
                "insert into system_agent_event_history (agent_id, message) VALUES (?, ?)",
                history.systemAgentId,
                history.message
        )

        return getSequence("system_agent_event_history")
    }

    override fun getLastNumberItems(systemAgentId: Long, size: Long): List<SystemAgentEventHistory> {
        return jdbcTemplate.query(
                "select * from system_agent_event_history where agent_id = ? ORDER BY create_date ASC limit 0, ?",
                SystemAgentEventHistoryRowMapper(),
                systemAgentId,
                size
        )
    }
}
