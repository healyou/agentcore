package com.mycompany.db.jdbc.systemagent

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.db.jdbc.file.dslfile.DslFileAttachmentDao
import com.mycompany.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class SystemAgentServiceImpl : SystemAgentService {

    @Autowired
    private lateinit var dao: SystemAgentDao
    @Autowired
    private lateinit var dslDao: DslFileAttachmentDao

    override fun save(systemAgent: SystemAgent): Long {
        return if (systemAgent.isNew) {
            dao.create(systemAgent)
        }
        else {
            dao.update(systemAgent)
            systemAgent.id!!
        }
    }

    override fun getDslAttachment(systemAgentServiceLogin: String): DslFileAttachment? {
        return dslDao.getDslWorkingFileBySystemAgentServiceLogin(systemAgentServiceLogin)
    }

    override fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent> =
            dao.get(isDeleted, isSendAndGetMessages)

    override fun get(sc: SystemAgentSC): List<SystemAgent> = dao.get(sc)

    override fun get(size: Long, ownerId: Long): List<SystemAgent> {
        return dao.get(size, ownerId)
    }

    override fun getById(id: Long): SystemAgent {
        return dao.getById(id)
    }

    override fun getByServiceLogin(serviceLogin: String): SystemAgent = dao.getByServiceLogin(serviceLogin)

    override fun isExistsAgent(serviceLogin: String): Boolean = dao.isExistsAgent(serviceLogin)

    override fun isOwnAgent(agent: SystemAgent, user: User): Boolean {
        return dao.isOwnAgent(agent, user)
    }

    override fun size(): Long {
        return dao.size()
    }

    override fun get(size: Long): List<SystemAgent> {
        return dao.get(size)
    }
}