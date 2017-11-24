package db.jdbc.systemagent

import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgent

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentDao {

    fun create(systemAgent: SystemAgent): Long

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>

    fun get(sc: SystemAgentSC): List<SystemAgent>

    fun getByServiceLogin(serviceLogin: String): SystemAgent

    fun isExistsAgent(serviceLogin: String): Boolean
}