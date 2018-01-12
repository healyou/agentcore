package com.mycompany.db.core.servicemessage

import com.mycompany.db.core.sc.ServiceMessageSC

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

    /**
     * Получить последние n записей для агента
     */
    fun getLastNumberItems(systemAgentId: Long, size: Long): List<ServiceMessage>
}