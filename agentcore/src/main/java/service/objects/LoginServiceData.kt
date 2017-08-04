package service.objects

/**
 * Классы параметры запросов к логин сервису агентов
 *
 * @author Nikita Gorodilov
 */

/**
 * Данные для логина
 */
class LoginData(val masId: String, val password: String)

/**
 * Данные для регистрации
 */
class RegistrationData(val masId: String, val name: String, val type: String,  val password: String)

