package com.mycompany.objects

import com.mycompany.db.core.systemagent.SystemAgent

/**
 * @author Nikita Gorodilov
 */
class SystemAgentObjects {

    static final def systemAgent() {
        systemAgent(1L, 1L, 1L)
    }

    static final systemAgent(Long id, Long ownerId, Long createUserId) {
        def systemAgent = new SystemAgent(StringObjects.randomString, StringObjects.randomString, true, ownerId, createUserId)
        systemAgent.id = id
        systemAgent.createDate = new Date()
        systemAgent.dslFile = null
        systemAgent.isDeleted = false
        systemAgent
    }
}
