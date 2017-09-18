package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServiceMessageTypeServiceImpl : ServiceMessageTypeService {

    @Autowired
    private lateinit var dao: ServiceMessageTypeDao

    override fun get(code: ServiceMessageType.Code): ServiceMessageType {
        return dao.get(code)
    }
}