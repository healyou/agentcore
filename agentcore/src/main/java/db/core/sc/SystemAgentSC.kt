package db.core.sc

/**
 * Параметры для поиска системных агентов в базе данных
 *
 * @author Nikita Gorodilov
 */
class SystemAgentSC {

    /* Удалён ли агент */
    var isDeleted: Boolean? = null
    /* Нужно ли получать и отправлять сообщения данного агента */
    var isSendAndGetMessages: Boolean? = null
}