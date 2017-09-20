package service.tasks

import org.springframework.beans.factory.annotation.Autowired
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.*
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
        private val localMessageService: ServiceMessageService
) {

    // TODO - интерсептор на куки
    // TODO - Сервис получения и отправки сообщений на сервис агентов
    // TODO - логин и пароль для входа из бд + привязка чтение к нескольким агентам - надо ли?

    // TODO тесты для сообщений - system_agent_id добавлен
    // TODO переделать чтение и отправку сообщений под n агентов

    init {
    }

    /**
     * Получение сообщений с сервиса агентов
     */
    @Scheduled(fixedDelay=15000)
    fun getMessages() {
        System.out.println("getMessages - ServiceTask")

        val sessionManager = SessionManager()
        val agent = loginService.login(LoginData("masId", "psw"), sessionManager)
        if (agent != null) {
            val messages = serverMessageService.getMessages(sessionManager, GetMessagesData(
                    null, null, null, null, false, null, null
            ))

            /* Сохраняем сообщения в бд */
            messages
                    ?.map { it -> { ServiceMessage(
                            AbstractAgentService.toJson(it),
                            messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE),
                            messageTypeService.get(ServiceMessageType.Code.SEND)
                    )}}
                    ?.forEach { it ->
                        localMessageService.save(it.invoke())
                    }
        }
    }

    /**
     * Отправка сообщений на сервис агентов
     */
    @Scheduled(fixedDelay=15000)
    fun sendMessages() {
        System.out.println("sendMessages - ServiceTask")

        val sessionManager = SessionManager()
        val agent = loginService.login(LoginData("masId", "psw"), sessionManager)
        if (agent != null) {
            val sc = ServiceMessageSC()
            sc.isUse = false
            sc.messageType = messageTypeService.get(ServiceMessageType.Code.SEND)

            val messages = localMessageService.get(sc)
            println(Arrays.toString(messages.toTypedArray()))

            val recipients = serverAgentService
                    .getAgents(
                            sessionManager,
                            GetAgentsData(
                                    null,//AgentType.Code.SERVER.code,
                                    false
                            )
                    )
                    ?.map {
                        it.id!!
                    }

            /* Отправка сообщений */
            if (recipients != null) {
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
        }
    }
}