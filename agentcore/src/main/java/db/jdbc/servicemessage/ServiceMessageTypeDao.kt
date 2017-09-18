package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessageType

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageTypeDao {

    fun get(code: ServiceMessageType.Code): ServiceMessageType
}