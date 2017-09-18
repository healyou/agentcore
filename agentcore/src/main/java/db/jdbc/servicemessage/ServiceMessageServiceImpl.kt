package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
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
        if (message.isNew) {
            return dao.create(message)

        } else {
            return dao.update(message)
        }
    }

    override fun use(message: ServiceMessage) {
        dao.use(message)
    }
}