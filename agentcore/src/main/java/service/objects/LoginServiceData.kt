package service.objects

import java.util.*

/**
 * Классы параметры запросов к логин сервису агентов
 *
 * @author Nikita Gorodilov
 */

/**
 * Данные для логина
 */
data class LoginData(
        val masId: String,
        val password: String
)

/**
 * Данные для регистрации
 */
data class RegistrationData(
        val masId: String,
        val name: String,
        val type: String,
        val password: String
)

/**
 * Данные для поиска агента
 */
data class GetAgentsData(
        val type: String? = null,
        val isDeleted: Boolean? = null
)

/**
 * Данные для отправки сообщения
 */
data class SendMessageData(
        val goalType: String,
        val type: String,
        val recipientsIds: List<Long>,
        val bodyType: String,
        val body: String
)

/**
 * Данные для получения сообщений
 */
data class GetMessagesData(
        val goalType: String? = null,
        val type: String? = null,
        val bodyType: String? = null,
        val senderId: Long? = null,
        val isViewed: Boolean? = null,
        val sinceCreatedDate: Date? = null,
        val sinceViewedDate: Date? = null
)