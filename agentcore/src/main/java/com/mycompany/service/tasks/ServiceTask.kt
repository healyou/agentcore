package com.mycompany.service.tasks

import com.mycompany.db.base.Utils
import org.springframework.beans.factory.annotation.Autowired
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.*
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import com.mycompany.service.*
import com.mycompany.service.objects.*

/**
 * Класс получения и отправки сообщений в сервис агентов
 *      Получение и отправки идут из локальной бд агента
 *
 * @author Nikita Gorodilov
 */
@Component
class ServiceTask @Autowired constructor(
        private val loginService: LoginService,
        private val serverAgentService: ServerAgentService,
        private val serverMessageService: ServerMessageService,
        private val messageTypeService: ServiceMessageTypeService,
        private val localMessageService: ServiceMessageService,
        private val systemAgentService: SystemAgentService,
        private val workControl: IRuntimeAgentWorkControl
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Получение сообщений с сервиса агентов
     */
    @Scheduled(fixedDelay=20000)
    fun getMessages() {
        System.out.println("Процесс получения сообщений")
        logger.debug("Старт - Процесс получения сообщений")

        /* Список локальных агентов, для которых надо считать сообщения */
        getSystemAgents().forEach { it ->
            val sessionManager = SessionManager()

            /* При удачном логине */
            if (isSuccessLogin(it, sessionManager)) {
                /* Читаем все свои сообщения */
                readMessages(it, sessionManager)

                /* Выход с сервера */
                loginService.logout(sessionManager)
            }
        }

        logger.debug("Конец - Процесс получения сообщений")
    }

    /**
     * Отправка сообщений на сервис агентов
     */
    @Scheduled(fixedDelay=20000)
    fun sendMessages() {
        System.out.println("Процесс отправки сообщений")
        logger.debug("Старт - Процесс отправки сообщений")

        /* Список локальных агентов, от которых надо отправить сообщения */
        getSystemAgents().forEach { it ->
            val sessionManager = SessionManager()

            /* При удачном логине */
            if (isSuccessLogin(it, sessionManager)) {
                /* Отправка сообщений */
                sendMessages(it, sessionManager)

                /* Выход с сервера */
                loginService.logout(sessionManager)
            }
        }

        logger.debug("Конец - Процесс отправки сообщений")
    }

    /**
     * Логин агента в сервисе
     */
    private fun isSuccessLogin(systemAgent: SystemAgent, sessionManager: SessionManager): Boolean {
        return if (loginService.login(
                LoginData(systemAgent.serviceLogin, systemAgent.servicePassword),
                sessionManager
        ) != null) {
            logger.debug("Агент[id = ${systemAgent.id}] - успешный логин в сервисе")
            true

        } else {
            logger.debug("Агент[id = ${systemAgent.id}] - неудачная попытка логина в сервисе")
            false
        }
    }

    /**
     * Список локальный агентов
     * @return список агентов, запущенных в текущем приложении
     */
    private fun getSystemAgents(): List<SystemAgent> {
        return workControl.getStartedAgents()
    }

    /**
     * Чтение сообщений(нужно перед этим залогиниться)
     */
    private fun readMessages(systemAgent: SystemAgent, sessionManager: SessionManager) {
        /* Читаем все свои сообщения */
        val messages = serverMessageService.getMessages(sessionManager, GetMessagesData(
                null, null, null, null, false, null, null
        ))
        logger.debug("Агент[id = ${systemAgent.id}] - с сервиса агентов считано ${messages?.size ?: 0} сообщений")

        /* Сохраняем сообщения в бд, если такие есть */
        messages
                ?.map { it -> {
                    val serviceMessage = ServiceMessage(
                        AbstractAgentService.toJson(it),
                        messageTypeService.get(ServiceMessageType.Code.GET),
                        systemAgent.id!!
                    )
                    serviceMessage.getMessageSenderCode = it.sender?.type?.code
                    serviceMessage
                }}
                ?.forEach { it ->
                    localMessageService.save(it.invoke())
                }
    }

    /**
     * Отправка сообщений агента(нужно перед этим залогиниться)
     */
    private fun sendMessages(systemAgent: SystemAgent, sessionManager: SessionManager) {
        val sc = ServiceMessageSC()
        sc.isUse = false
        sc.messageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        sc.systemAgentId = systemAgent.id!!

        /* Считываем сообщения лишь нашего агента */
        val messages = localMessageService.get(sc)
        logger.debug("Агент[id = ${systemAgent.id}] - считано локальный сообщений для отправки - ${messages.size}")

        /* Отправка сообщений */
        messages.forEach {
            if (!Utils.isOneNull(it.sendMessageType, it.sendMessageBodyType)) {
                serverMessageService.sendMessage(
                        sessionManager,
                        SendMessageData(
                                it.sendMessageType!!,
                                getMessageRecipientsIds(it, systemAgent, sessionManager),
                                it.sendMessageBodyType!!,
                                it.messageBody
                        )
                )
                localMessageService.use(it)
            } else {
                logger.debug("Агент[id = ${systemAgent.id}] - невозможно отправить сообщения - нехватка данных")
                throw RuntimeException("Ошибка отправки сообщения - нехватка данных")
            }
        }
    }

    /**
     * Список получателей сообщения
     */
    private fun getMessageRecipientsIds(serviceMessage: ServiceMessage,
                                        systemAgent: SystemAgent,
                                        sessionManager: SessionManager): List<Long> {
        val agentCodes = serviceMessage.sendAgentTypeCodes
        val recipients = arrayListOf<Long>()

        agentCodes!!.forEach { itAgentCode ->
            serverAgentService
                    .getAgents(
                            sessionManager,
                            GetAgentsData(
                                    itAgentCode,
                                    false
                            )
                    )
                    ?.forEach {
                        recipients.add(it.id!!)
                    }
        }

        logger.debug("Агент[id = ${systemAgent.id}] - считано агентов с сервиса, которым надо отправлять сообщения - ${recipients.size}")
        return recipients
    }
}