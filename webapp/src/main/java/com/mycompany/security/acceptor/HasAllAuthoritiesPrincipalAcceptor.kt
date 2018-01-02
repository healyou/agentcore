package com.mycompany.security.acceptor

import com.mycompany.user.Authority
import com.mycompany.user.Principal
import java.util.*

/**
 * Для возврата true необходимо иметь все привилегии
 *
 * @author Nikita Gorodilov
 */
class HasAllAuthoritiesPrincipalAcceptor(vararg private val authorities: Authority): PrincipalAcceptor {

    override fun accept(principal: Principal): Boolean {
        return Arrays.stream(authorities).allMatch(principal.authorities::contains);
    }
}
