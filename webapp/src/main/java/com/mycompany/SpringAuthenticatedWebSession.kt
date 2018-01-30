package com.mycompany

import com.mycompany.db.core.user.AuthenticationService
import com.mycompany.dsl.exceptions.AuthenticationException
import com.mycompany.user.Principal
import org.apache.wicket.Application
import org.apache.wicket.Localizer
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.authroles.authorization.strategies.role.Roles
import org.apache.wicket.injection.Injector
import org.apache.wicket.request.Request
import org.apache.wicket.spring.injection.annot.SpringBean

/**
 * @author Nikita Gorodilov
 */
open class SpringAuthenticatedWebSession(request: Request) : AuthenticatedWebSession(request) {

    /**
     * авторизация
     */
    @SpringBean
    private lateinit var authenticationService: AuthenticationService;

    /**
     * У него внутри там права доступа
     */
    /**
     * Получить текущего авторизованного пользователя
     *
     * @return текущий авторизованный пользователь
     */
    var principal: Principal? = null

    init {
        Injector.get().inject(this)
    }

    override fun authenticate(login: String, password: String): Boolean {
        return try {
            principal = authenticationService.authenticate(login, password)
            true
        } catch (e: AuthenticationException) {
            error(e.message)
            false
        }
    }

    override fun getRoles(): Roles {
        val roles = Roles()
        if (isSignedIn) {
            roles.add("POSTGRES_USER_ROLE")
        }
        return roles
    }

    override fun signOut() {
        //user = null;
        super.signOut()
    }
}
