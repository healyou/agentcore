package db.core.servicemessage

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
    // TODO методы получения сообщений по его типу
}