package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageObjectTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServiceMessageObjectTypeServiceImpl : ServiceMessageObjectTypeService {

    @Autowired
    private lateinit var dao: ServiceMessageObjectTypeDao

    override fun get(code: ServiceMessageObjectType.Code): ServiceMessageObjectType {
        return dao.get(code)
    }
}