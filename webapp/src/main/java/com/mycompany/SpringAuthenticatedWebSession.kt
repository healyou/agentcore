package com.mycompany

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.authroles.authorization.strategies.role.Roles
import org.apache.wicket.injection.Injector
import org.apache.wicket.request.Request

/**
 * @author Nikita Gorodilov
 */
class SpringAuthenticatedWebSession(request: Request) : AuthenticatedWebSession(request) {

    /**
     * авторизация
     */
//    @SpringBean
//    private AuthenticationService authenticationService;

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

    override fun authenticate(username: String, password: String): Boolean {
        //        try {
        //            principal = authenticationService.authenticate(username, password);
        //            return true;
        //        } catch (AuthenticationException e) {
        //            error(getString(e.getClass().getSimpleName(), e.getMessage()));
        //            logger.info("Error while authenticating user {}: ", username, e);
        //            return false;
        //        }

        val WICKET = "wicket"
        if (WICKET == username && WICKET == password) {
            principal = Principal()
            return true
        }
        return false
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
