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
//TODO тесты для работы сообщений
    /**
     * Использование сообщения
     */
    fun use(message: ServiceMessage)

    fun get(sc: ServiceMessageSC) : List<ServiceMessage>
}