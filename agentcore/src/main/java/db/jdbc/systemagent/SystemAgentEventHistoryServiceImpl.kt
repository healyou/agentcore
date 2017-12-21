package db.jdbc.systemagent

import db.core.systemagent.SystemAgentEventHistory
import db.core.systemagent.SystemAgentEventHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
class SystemAgentEventHistoryServiceImpl: SystemAgentEventHistoryService {

    @Autowired
    private lateinit var dao: SystemAgentEventHistoryDao
    
    override fun create(history: SystemAgentEventHistory): Long {
        return dao.create(history)
    }

    override fun getLastNumberItems(systemAgentId: Long, size: Long): List<SystemAgentEventHistory> {
        return dao.getLastNumberItems(systemAgentId, size)
    }
}