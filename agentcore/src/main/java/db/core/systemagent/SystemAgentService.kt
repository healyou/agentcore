package db.core.systemagent

import db.core.sc.SystemAgentSC

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentService {

    fun save(systemAgent: SystemAgent): Long

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>

    fun get(sc: SystemAgentSC): List<SystemAgent>

    fun get(id: Long): SystemAgent

    fun getByServiceLogin(serviceLogin: String): SystemAgent

    fun isExistsAgent(serviceLogin: String): Boolean
}