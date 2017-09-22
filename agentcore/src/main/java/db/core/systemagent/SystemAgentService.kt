package db.core.systemagent

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentService {

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>
}