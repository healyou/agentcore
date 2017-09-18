package db.jdbc.servicemessage

import db.core.servicemessage.ServiceMessage

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageDao {

    fun create(message: ServiceMessage): Long
    fun update(message: ServiceMessage)
    fun use(message: ServiceMessage)
}