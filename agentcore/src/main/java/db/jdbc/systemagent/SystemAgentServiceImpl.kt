package db.jdbc.systemagent

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

    override fun get(isDeleted: Boolean): List<SystemAgent> {
        return dao.get(isDeleted)
    }
}