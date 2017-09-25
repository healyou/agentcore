package service.tasks

import org.springframework.beans.factory.annotation.Autowired
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.*
import db.core.systemagent.SystemAgentService
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

    // TODO - интерсептор на куки (МОЖЕТ ПОДОЖДАТЬ)
    // TODO system_agent_id в сообщении на полного агента переделать (МОЖЕТ ПОДОЖДАТЬ)
    // TODO логирование на отправку сообщений (МОЖЕТ ПОДОЖДАТЬ)

    // TODO - переписать эту часть на читабельный вид (ВНАЧАЛЕ)
    // TODO - задание по изображениям выбрать и начать продумывать его
    // TODO - GUI хоть какое то придумать под запросы разные, тиблички из бд и другое
    init {
    }

    /**
     * Получение сообщений с сервиса агентов
     */
    @Scheduled(fixedDelay=30000)
    fun getMessages() {
        System.out.println("getMessages - ServiceTask")

        /* Список всех локальных агентов */
        val systemAgents = systemAgentService.get(false, true)

        /* Для всех агентов */
        systemAgents.forEach { itSystemAgent ->
            val sessionManager = SessionManager()
            /* Вход на сервер */
            val agent = loginService.login(
                    LoginData(itSystemAgent.serviceLogin, itSystemAgent.servicePassword),
                    sessionManager
            )

            /* При удачном логине */
            if (agent != null) {
                /* Читаем все свои сообщения */
                val messages = serverMessageService.getMessages(sessionManager, GetMessagesData(
                        null, null, null, null, false, null, null
                ))

                /* Сохраняем сообщения в бд, если такие есть */
                messages
                        ?.map { it -> { ServiceMessage(
                                AbstractAgentService.toJson(it),
                                messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE),
                                messageTypeService.get(ServiceMessageType.Code.SEND),
                                itSystemAgent.id!!
                        )}}
                        ?.forEach { it ->
                            localMessageService.save(it.invoke())
                        }

                /* Выход с сервера */
                loginService.logout(sessionManager)
            }
        }
    }

    /**
     * Отправка сообщений на сервис агентов
     */
    @Scheduled(fixedDelay=30000)
    fun sendMessages() {
        System.out.println("sendMessages - ServiceTask")

        /* Список всех локальных агентов */
        val systemAgents = systemAgentService.get(false, true)

        /* Для всех агентов */
        systemAgents.forEach { itSystemAgent ->
            val sessionManager = SessionManager()
            /* Вход на сервер */
            val agent = loginService.login(
                    LoginData(itSystemAgent.serviceLogin, itSystemAgent.servicePassword),
                    sessionManager
            )

            /* При удачном логине */
            if (agent != null) {
                val sc = ServiceMessageSC()
                sc.isUse = false
                sc.messageType = messageTypeService.get(ServiceMessageType.Code.SEND)
                sc.systemAgentId = itSystemAgent.id!!

                /* Считываем сообщения лишь нашего агента */
                val messages = localMessageService.get(sc)

                /* Поиск агентов, которым надо отправлять данные */
                val agentCodes = itSystemAgent.sendAgentTypeCodes
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
        }
    }
}