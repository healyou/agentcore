package com.mycompany.service

import com.mycompany.service.objects.GetMessagesData
import com.mycompany.service.objects.Message
import com.mycompany.service.objects.SendMessageData

/**
 * @author Nikita Gorodilov
 */
interface ServerMessageService {

    fun sendMessage(sessionManager: SessionManager, data: SendMessageData): Message?

    fun getMessages(sessionManager: SessionManager, data: GetMessagesData): List<Message>?
}