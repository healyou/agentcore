package service

import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
interface ServerTypeService {

    fun getAgentTypes(sessionManager: SessionManager): List<AgentType>?

    fun getMessageBodyTypes(sessionManager: SessionManager): List<MessageBodyType>?

    fun getMessageGoalTypes(sessionManager: SessionManager): List<MessageGoalType>?

    fun getMessageTypes(sessionManager: SessionManager, goalType: String): List<MessageType>?

    // TODO - метод для получения всех MessageType сразу
}