package gui

import db.core.user.AuthenticationService
import dsl.exceptions.AuthenticationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import user.Principal

/**
 * Данные передаваемые между javaFx формами после авторизации пользователя
 *
 * @author Nikita Gorodilov
 */
@Component
class AuthenticatedJavaFxSession {

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    /* Авторизованный пользователь, доступен после авторизации */
    private var _principal: Principal? = null
    val principal: Principal
        get() = _principal!!

    @Throws(AuthenticationException::class)
    fun authenticate(username: String, password: String): Boolean {
        return try {
            _principal = authenticationService.authenticate(username, password)
            true
        } catch (e: AuthenticationException) {
            false
        }
    }
}