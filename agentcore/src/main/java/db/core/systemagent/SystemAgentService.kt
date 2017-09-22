package db.core.systemagent

import db.core.sc.SystemAgentSC

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentService {

    fun create(systemAgent: SystemAgent): Long

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>

    fun get(sc: SystemAgentSC): List<SystemAgent>
}