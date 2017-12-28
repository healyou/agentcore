package db.jdbc.user

import db.core.user.AuthenticationService
import dsl.exceptions.AuthenticationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import user.Authority
import user.Principal

/**
 * @author Nikita Gorodilov
 */
@Component
class AuthenticationServiceImpl: AuthenticationService {

    @Autowired
    private lateinit var principalDao: PrincipalDao

    @Throws(AuthenticationException::class)
    override fun authenticate(username: String, password: String): Principal {
        val principal = principalDao.getPrincipal(username)
        if (principal.user.password == password) {
            if (principal.authorities.contains(Authority.LOGIN)) {
                return principal
            } else {
                throw AuthenticationException("Нет прав для авторизации")
            }
        } else {
            throw AuthenticationException("Неверные данные для входа")
        }
    }
}