package com.mycompany.objects

import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageType

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageObjects {

    static final List<ServiceMessage> serviceMessages() {
        Arrays.asList(serviceMessage(), serviceMessage(), serviceMessage())
    }

    static final def serviceMessage() {
        def serviceMessage = new ServiceMessage(
                StringObjects.randomString,
                new ServiceMessageType(1L, ServiceMessageType.Code.GET, StringObjects.randomString, false),
                1L
        )
        serviceMessage.createDate = new Date()
        serviceMessage.id = 1L
        serviceMessage.getMessageSenderCode = StringObjects.randomString
        serviceMessage.sendAgentTypeCodes = Arrays.asList(StringObjects.randomString)
        serviceMessage.sendMessageType = StringObjects.randomString
        serviceMessage.sendMessageBodyType = StringObjects.randomString
        return serviceMessage
    }
}
