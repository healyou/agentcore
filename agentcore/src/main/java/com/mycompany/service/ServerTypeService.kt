package com.mycompany.service

import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.MessageBodyType
import com.mycompany.service.objects.MessageGoalType
import com.mycompany.service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
interface ServerTypeService {

    fun getAgentTypes(sessionManager: SessionManager): List<AgentType>?

    fun getMessageBodyTypes(sessionManager: SessionManager): List<MessageBodyType>?

    fun getMessageGoalTypes(sessionManager: SessionManager): List<MessageGoalType>?

    fun getMessageTypes(sessionManager: SessionManager, goalType: String): List<MessageType>?

    fun getMessageTypes(sessionManager: SessionManager): List<MessageType>?
}