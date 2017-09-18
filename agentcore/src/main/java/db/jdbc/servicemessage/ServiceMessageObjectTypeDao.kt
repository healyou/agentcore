package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessageObjectType

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageObjectTypeDao {

    fun get(code: ServiceMessageObjectType.Code): ServiceMessageObjectType
}