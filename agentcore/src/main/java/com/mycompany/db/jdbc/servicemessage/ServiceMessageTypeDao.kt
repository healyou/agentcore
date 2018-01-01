package com.mycompany.db.jdbc.servicemessage

import com.mycompany.db.core.servicemessage.ServiceMessageType

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageTypeDao {

    fun get(code: ServiceMessageType.Code): ServiceMessageType
}