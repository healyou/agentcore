package com.mycompany.dsl

import com.fasterxml.jackson.core.type.TypeReference
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.service.AbstractAgentService
import com.mycompany.service.objects.Message
import java.io.IOException
import java.util.*

/**
 * Поиск сообщений агента
 *
 * @author Nikita Gorodilov
 */
class AgentSearchMessageTimer(
        private val messageService: ServiceMessageService,
        private val messageTypeService: ServiceMessageTypeService,
        private val systemAgentId: Long,
        private val onGetServiceMessageCallback: RuntimeAgent.OnGetServiceMessageFunction
) {

    companion object {
        val FIRST_SEARCH_MESSAGE_DELAY = 1000L
        val SEARCH_MESSAGE_DELAY = 2000L
    }

    private var searchMessageTimer: Timer? = null

    fun start() {
        searchMessageTimer = Timer()
        searchMessageTimer!!.schedule(object : TimerTask() {
            override fun run() {
                searchMessages()
            }
        }, FIRST_SEARCH_MESSAGE_DELAY, SEARCH_MESSAGE_DELAY)
    }

    fun stop() {
        if (searchMessageTimer != null) {
            searchMessageTimer!!.cancel()
        }
    }

    /* Видимость оставлена для тестирования */
    protected fun searchMessages() {
        val readMessages = readMessages()
        readMessages.forEach {
            useMessage(it)
        }
    }

    private fun readMessages(): List<ServiceMessage> {
        val sc = configureSearchMessagesSC()
        return messageService.get(sc)
    }

    private fun configureSearchMessagesSC(): ServiceMessageSC {
        val sc = ServiceMessageSC()
        sc.systemAgentId = systemAgentId
        sc.isUse = false
        sc.messageType = messageTypeService.get(ServiceMessageType.Code.GET)
        return sc
    }

    private fun useMessage(serviceMessage: ServiceMessage) {
        messageService.use(serviceMessage)
        try {
            onGetServiceMessageCallback.onGetServiceMessage(configureDslServiceMessage(serviceMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun configureDslServiceMessage(serviceMessage: ServiceMessage): DslServiceMessage {
        val messageBody = parseServiceMessage(serviceMessage.messageBody).body
        val getMessageSenderCode = serviceMessage.getMessageSenderCode
        return DslServiceMessage(Objects.requireNonNull<String>(getMessageSenderCode), messageBody)
    }

    @Throws(IOException::class)
    private fun parseServiceMessage(jsonObject: String): Message {
        return AbstractAgentService.fromJson(jsonObject, object : TypeReference<Message>() {})
    }
}