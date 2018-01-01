package com.mycompany.db.jdbc.servicemessage

import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage

/**
 * @author Nikita Gorodilov
 */
interface ServiceMessageDao {

    fun create(message: ServiceMessage) : Long
    fun update(message: ServiceMessage) : Long
    fun use(message: ServiceMessage)
    fun get(sc: ServiceMessageSC) : List<ServiceMessage>
    fun get(id: Long): ServiceMessage
}