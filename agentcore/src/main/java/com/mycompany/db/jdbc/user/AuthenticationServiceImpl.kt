package com.mycompany.db.jdbc.user

import com.mycompany.db.core.user.AuthenticationService
import com.mycompany.dsl.exceptions.AuthenticationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.mycompany.user.Principal
import org.springframework.dao.EmptyResultDataAccessException

/**
 * @author Nikita Gorodilov
 */
@Component
class AuthenticationServiceImpl: AuthenticationService {

    @Autowired
    private lateinit var principalDao: PrincipalDao

    @Throws(AuthenticationException::class)
    override fun authenticate(username: String, password: String): Principal {
        try {
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
        } catch (e: EmptyResultDataAccessException) {
            throw AuthenticationException("Неверные данные для входа")
        }
    }
}