package service.tasks

import org.springframework.beans.factory.annotation.Autowired
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.*
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import service.*
import service.objects.*
import java.util.*

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
        private val messageObjectTypeService: ServiceMessageObjectTypeService,
        private val messageTypeService: ServiceMessageTypeService,
        private val localMessageService: ServiceMessageService,
        private val systemAgentService: SystemAgentService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // TODO - интерсептор на куки (МОЖЕТ ПОДОЖДАТЬ)
    // TODO system_agent_id в сообщении на полного агента переделать (МОЖЕТ ПОДОЖДАТЬ)

    // TODO - задание по изображениям выбрать и начать продумывать его
    init {
    }

    /**
     * Получение сообщений с сервиса агентов
     */
    @Scheduled(fixedDelay=30000)
    fun getMessages() {
        System.out.println("Процесс получения сообщений")
        logger.debug("Старт - Процесс получения сообщений")

        /* Список всех локальных агентов */
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
    @Scheduled(fixedDelay=30000)
    fun sendMessages() {
        System.out.println("Процесс отправки сообщений")
        logger.debug("Старт - Процесс отправки сообщений")

        /* Список всех локальных агентов */
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
     */
    private fun getSystemAgents(): List<SystemAgent> {
        return systemAgentService.get(false, true);
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
                ?.map { it -> { ServiceMessage(
                        AbstractAgentService.toJson(it),
                        messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE),
                        messageTypeService.get(ServiceMessageType.Code.GET),
                        Collections.emptyList(),
                        systemAgent.id!!
                )}}
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

        /* Поиск агентов, которым надо отправлять данные */
        val recipients = getMessageRecipientsIds(systemAgent, sessionManager)
        logger.debug("Агент[id = ${systemAgent.id}] - считано агентов с сервиса, которым надо отправлять сообщения - ${recipients.size}")

        /* Отправка сообщений */
        messages.forEach {
            serverMessageService.sendMessage(
                    sessionManager,
                    SendMessageData(
                            MessageGoalType.Code.TASK_DECISION.code,
                            MessageType.Code.SEARCH_SOLUTION.code,
                            recipients,
                            MessageBodyType.Code.JSON.code,
                            it.jsonObject
                    )
            )
            localMessageService.use(it)
        }
    }

    /**
     * Список получателей сообщения
     */
    private fun getMessageRecipientsIds(systemAgent: SystemAgent, sessionManager: SessionManager): List<Long> {
        val agentCodes = Collections.emptyList<AgentType.Code>()
        val recipients = arrayListOf<Long>()

        agentCodes.forEach { itAgentCode ->
            serverAgentService
                    .getAgents(
                            sessionManager,
                            GetAgentsData(
                                    itAgentCode.code,
                                    false
                            )
                    )
                    ?.forEach {
                        recipients.add(it.id!!)
                    }
        }

        return recipients
    }
}