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
    fun save(message: ServiceMessage)
//TODO тесты для работы сообщений
    /**
     * Использование сообщения
     */
    fun use(message: ServiceMessage)
}