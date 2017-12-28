package db.jdbc.user

import db.core.user.AuthenticationService
import dsl.exceptions.AuthenticationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
            if (!principal.user.isDeleted) {
                return principal
            } else {
                throw AuthenticationException("Пользователь удалён")
            }
        } else {
            throw AuthenticationException("Неверные данные для входа")
        }
    }
}