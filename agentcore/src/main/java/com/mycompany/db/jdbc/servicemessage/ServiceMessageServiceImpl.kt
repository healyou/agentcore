package com.mycompany.db.jdbc.servicemessage

import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServiceMessageServiceImpl : ServiceMessageService {

    @Autowired
    private lateinit var dao: ServiceMessageDao

    override fun save(message: ServiceMessage) : Long {
        return if (message.isNew) {
            dao.create(message)

        } else {
            dao.update(message)
        }
    }

    override fun use(message: ServiceMessage) {
        dao.use(message)
    }

    override fun get(sc: ServiceMessageSC) : List<ServiceMessage> {
        return dao.get(sc)
    }

    override fun get(id: Long): ServiceMessage {
        return dao.get(id)
    }

    override fun getLastNumberItems(systemAgentId: Long, size: Long): List<ServiceMessage> {
        return dao.getLastNumberItems(systemAgentId, size)
    }
}