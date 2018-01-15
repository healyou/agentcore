package com.mycompany.db.jdbc.systemagent

import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.user.User

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentDao {

    fun create(systemAgent: SystemAgent): Long

    fun update(systemAgent: SystemAgent): Long

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>

    fun get(sc: SystemAgentSC): List<SystemAgent>

    fun get(size: Long, ownerId: Long): List<SystemAgent>

    fun getById(id: Long): SystemAgent

    fun getByServiceLogin(serviceLogin: String): SystemAgent

    fun isExistsAgent(serviceLogin: String): Boolean

    fun isOwnAgent(agent: SystemAgent, user: User): Boolean

    fun size(ownerId: Long): Long

    fun size(): Long

    fun get(size: Long): List<SystemAgent>
}