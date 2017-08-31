package service

import service.objects.GetMessagesData
import service.objects.Message
import service.objects.SendMessageData

/**
 * @author Nikita Gorodilov
 */
interface ServerMessageService {

    fun sendMessage(sessionManager: SessionManager, data: SendMessageData): Message?

    fun getMessages(sessionManager: SessionManager, data: GetMessagesData): List<Message>?
}