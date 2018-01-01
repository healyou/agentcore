package com.mycompany.db.jdbc.systemagent

import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentDao {

    fun create(systemAgent: SystemAgent): Long

    fun update(systemAgent: SystemAgent): Long

    fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent>

    fun get(sc: SystemAgentSC): List<SystemAgent>

    fun get(id: Long): SystemAgent

    fun getByServiceLogin(serviceLogin: String): SystemAgent

    fun isExistsAgent(serviceLogin: String): Boolean
}