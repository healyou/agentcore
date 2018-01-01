package com.mycompany.db.jdbc.servicemessage

import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
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