package db.jdbc.systemagent

import db.core.systemagent.SystemAgentEventHistory

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentEventHistoryDao {

    fun create(history: SystemAgentEventHistory): Long

    fun getLastNumberItems(size: Long): List<SystemAgentEventHistory>
}