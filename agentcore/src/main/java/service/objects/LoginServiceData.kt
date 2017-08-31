package service.objects

/**
 * Классы параметры запросов к логин сервису агентов
 *
 * @author Nikita Gorodilov
 */

/**
 * Данные для логина
 */
data class LoginData(val masId: String, val password: String)

/**
 * Данные для регистрации
 */
data class RegistrationData(val masId: String, val name: String, val type: String,  val password: String)

/**
 * Данные для поиска агента
 */
data class GetAgentsData(val type: String?, val isDeleted: Boolean?)
