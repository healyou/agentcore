package com.mycompany.security

import com.mycompany.SpringAuthenticatedWebSession
import com.mycompany.security.acceptor.HasAllAuthoritiesPrincipalAcceptor
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.user.Authority
import com.mycompany.user.Principal
import org.apache.wicket.Session

/**
 * @author Nikita Gorodilov
 */
interface PrincipalSupport {

    fun getPrincipal(): Principal {
        return (Session.get() as SpringAuthenticatedWebSession).principal!!
    }

    fun setPrincipal(principal: Principal) {
        (Session.get() as SpringAuthenticatedWebSession).principal = principal
    }

    fun isPrincipalHasAnyAuthority(vararg authorities: Authority): Boolean {
        return HasAnyAuthorityPrincipalAcceptor(*authorities).accept(getPrincipal());
    }

    fun isPrincipalHasAllAuthorities(vararg authorities: Authority): Boolean {
        return HasAllAuthoritiesPrincipalAcceptor(*authorities).accept(getPrincipal());
    }
}
