package db.core.sc

import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageType

/**
 * Параметры для поиска сообщений в базе данных
 * 
 * @author Nikita Gorodilov
 */
class ServiceMessageSC {
    
    /* Тип сообщения */
    var messageType : ServiceMessageType? = null
    /* Использовалось ли это сообщение */
    var isUse : Boolean? = null
}