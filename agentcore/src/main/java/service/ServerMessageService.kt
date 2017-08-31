package service

import service.objects.Message
import service.objects.SendMessageData

/**
 * @author Nikita Gorodilov
 */
interface ServerMessageService {

    fun sendMessage(sessionManager: SessionManager, data: SendMessageData): Message?

    fun getMessages(sessionManager: SessionManager): List<Message>?
}