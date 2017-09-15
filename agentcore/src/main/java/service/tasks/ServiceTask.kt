package service.tasks

import org.springframework.beans.factory.annotation.Autowired
import agentcore.database.base.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import service.ServerMessageService

/**
 * Класс получения и отправки сообщений в сервис агентов
 *      Получение и отправки идут из локальной бд агента
 *
 * @author Nikita Gorodilov
 */
@Component
class ServiceTask @Autowired constructor(private val messageService: ServerMessageService, environment: Environment) {

    // TODO - классы для работы с локальной бд агента как сервисом спринга
    // TODO - наладка получения и отправки сообщений с сервиса из лок бд агента

    init {
    }

    /**
     * Получение сообщений с сервиса агентов
     */
    @Scheduled(fixedDelay=1000)
    fun getMessages() {
        System.out.println("getMessages - ServiceTask")
    }

    /**
     * Отправка сообщений на сервис агентов
     */
    @Scheduled(fixedDelay=1000)
    fun sendMessages() {
        System.out.println("sendMessages - ServiceTask")
    }
}