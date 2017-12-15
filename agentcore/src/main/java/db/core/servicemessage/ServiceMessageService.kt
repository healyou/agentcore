package db.core.servicemessage

import db.core.sc.ServiceMessageSC

/**
 * Работа с сообщениями
 *
 * @author Nikita Gorodilov
 */
interface ServiceMessageService {

    /**
     * Сохранение сообщения
     */
    fun save(message: ServiceMessage) : Long

    /**
     * Использование сообщения
     */
    fun use(message: ServiceMessage)

    /**
     * Получение сообщений
     */
    fun get(sc: ServiceMessageSC) : List<ServiceMessage>

    /**
     * Получения сообщения по id
     */
    fun get(id: Long): ServiceMessage
}