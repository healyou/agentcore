package com.mycompany.dsl.base

import com.fasterxml.jackson.core.type.TypeReference
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslMessage
import com.mycompany.service.AbstractAgentService
import com.mycompany.service.objects.Message
import java.util.*
import kotlin.concurrent.timer

/**
 * Класс агента, выполняющего работу над изображением
 * Конфигурируется с помощью dsl на groovy
 *
 * @author Nikita Gorodilov
 */
abstract class ARuntimeAgent : IRuntimeAgent {

    /**
     * Начал ли агент свою работу
     */
    var isStarted = false

    private var systemAgent: SystemAgent? = null
    private var getMessagesTimer: Timer? = null

    override fun start() {
        getMessagesTimer = timer("hello-timer", true, 1000, 2000) {
            searchMessages()
        }
        isStarted = true
    }

    override fun stop() {
        getMessagesTimer?.cancel()
        isStarted = false
    }

    /**
     * Поиск сообщений для текущего агента от других агентов
     */
    protected fun searchMessages() {
        val systemAgent = getSystemAgent() ?: return
        val messageService = getServiceMessageService()

        val sc = ServiceMessageSC()
        sc.systemAgentId = systemAgent.id
        sc.isUse = false
        sc.messageType = getMessageTypeService().get(ServiceMessageType.Code.GET)

        messageService.get(sc).forEach {
            messageService.use(it)
            onGetMessage(configureDslServiceMessage(it))
        }
    }

    protected abstract fun getSystemAgentService(): SystemAgentService
    protected abstract fun getServiceMessageService(): ServiceMessageService
    protected abstract fun getMessageTypeService(): ServiceMessageTypeService
    abstract fun getSystemAgent(): SystemAgent

    /**
     * Получаем сообщение, которые легко испоьзовать в dsl
     */
    private fun configureDslServiceMessage(serviceMessage: ServiceMessage): DslMessage {
        val jsonDslImage = parseServiceMessage(serviceMessage.jsonObject).body

        return DslMessage(
                serviceMessage.getMessageSenderCode!!,
                AbstractAgentService.fromJson(jsonDslImage, object : TypeReference<DslImage>() {})
        )
    }

    private fun parseServiceMessage(jsonObject: String): Message {
        return AbstractAgentService.fromJson(jsonObject, object : TypeReference<Message>() {})
    }
}