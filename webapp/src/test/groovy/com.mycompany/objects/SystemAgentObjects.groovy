package com.mycompany.objects

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.user.User

/**
 * @author Nikita Gorodilov
 */
class SystemAgentObjects {

    static final def systemAgent() {
        def user = UserObjects.user()
        systemAgent(1L, user, user)
    }

    static final systemAgent(Long id, User owner, User createUser) {
        def systemAgent = new SystemAgent(StringObjects.randomString, StringObjects.randomString, true, owner, createUser)
        systemAgent.id = id
        systemAgent.createDate = new Date()
        systemAgent.dslFile = DslFileAttachmentObjects.dslFileAttachment()
        systemAgent.isDeleted = false
        systemAgent
    }
}
