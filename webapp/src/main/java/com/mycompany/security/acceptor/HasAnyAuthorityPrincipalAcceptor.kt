package com.mycompany.security.acceptor

import com.mycompany.user.Authority
import com.mycompany.user.Principal
import java.util.*

/**
 * Для возврата true необходимо иметь хотя бы одну привилегию из списка
 *
 * @author Nikita Gorodilov
 */
class HasAnyAuthorityPrincipalAcceptor(vararg private val authorities: Authority): PrincipalAcceptor {

    override fun accept(principal: Principal): Boolean {
        return Arrays.stream(authorities).anyMatch(principal.authorities::contains);
    }
}