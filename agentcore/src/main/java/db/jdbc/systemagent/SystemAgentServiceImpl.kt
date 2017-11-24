package db.jdbc.systemagent

import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class SystemAgentServiceImpl : SystemAgentService {

    @Autowired
    private lateinit var dao: SystemAgentDao

    override fun create(systemAgent: SystemAgent): Long = dao.create(systemAgent)

    override fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent> =
            dao.get(isDeleted, isSendAndGetMessages)

    override fun get(sc: SystemAgentSC): List<SystemAgent> = dao.get(sc)

    override fun getByServiceLogin(serviceLogin: String): SystemAgent = dao.getByServiceLogin(serviceLogin)

    override fun isExistsAgent(serviceLogin: String): Boolean = dao.isExistsAgent(serviceLogin)
}