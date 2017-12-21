package db.core.systemagent

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentEventHistoryService {

    fun create(history: SystemAgentEventHistory): Long

    fun getLastNumberItems(size: Long): List<SystemAgentEventHistory>
}