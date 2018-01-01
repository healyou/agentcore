package com.mycompany.db.core.servicemessage

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageTypeService {

    fun get(code: ServiceMessageType.Code): ServiceMessageType
}