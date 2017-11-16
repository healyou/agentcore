package dsl

import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessageService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
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
//        getMessagesTimer = timer("hello-timer", true, 2000, 2000) {
//            searchMessages()
//        }
    }

    /**
     * Поиск сообщений для текущего агента
     */
    private fun searchMessages() {
        if (systemAgent == null) {
            return
        }

        val messageService = getServiceMessageService()

        val sc = ServiceMessageSC()
        sc.systemAgentId = systemAgent!!.id
        sc.isUse = false

        messageService.get(sc).forEach {
            onGetMessage(it)
        }
    }

    protected abstract fun getSystemAgentService(): SystemAgentService
    protected abstract fun getServiceMessageService(): ServiceMessageService
}