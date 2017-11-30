package dsl.base

import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import dsl.objects.DslMessage
import dsl.objects.DslImage
import service.AbstractAgentService
import java.util.*
import kotlin.concurrent.timer

/**
 * Класс агента, выполняющего работу над изображением
 * Конфигурируется с помощью dsl на groovy
 *
 * @author Nikita Gorodilov
 */
abstract class ARuntimeAgent : IRuntimeAgent {

    private var systemAgent: SystemAgent? = null
    private var getMessagesTimer: Timer? = null

    init {
        getMessagesTimer = timer("hello-timer", true, 2000, 2000) {
            searchMessages()
        }
    }

    /**
     * Поиск сообщений для текущего агента
     */
    private fun searchMessages() {
        val systemAgent = getSystemAgent() ?: return
        val messageService = getServiceMessageService()

        val sc = ServiceMessageSC()
        sc.systemAgentId = systemAgent.id
        sc.isUse = false

        messageService.get(sc).forEach {
            messageService.use(it)
            onGetMessage(configureDslServiceMessage(it))
        }
    }

    protected abstract fun getSystemAgentService(): SystemAgentService
    protected abstract fun getServiceMessageService(): ServiceMessageService
    protected abstract fun getSystemAgent(): SystemAgent?

    /**
     * Получаем сообщение, которые легко испоьзовать в dsl
     */
    private fun configureDslServiceMessage(serviceMessage: ServiceMessage): DslMessage {
        return DslMessage(
                serviceMessage.senderCode!!.code,
                AbstractAgentService.fromJson(serviceMessage.jsonObject, DslImage::class.java)
        )
    }
}