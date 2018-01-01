package com.mycompany

import org.apache.wicket.Session

/**
 * @author Nikita Gorodilov
 */
interface PrincipalSupport {

    //PrincipalHolder.setPrincipal(principal);// нафига это надо?

    fun getPrincipal(): Principal {
        return (Session.get() as SpringAuthenticatedWebSession).principal!!
    }

    fun setPrincipal(principal: Principal) {
        //PrincipalHolder.setPrincipal(principal);// нафига это надо?
        (Session.get() as SpringAuthenticatedWebSession).principal = principal
    }

    //    default boolean isPrincipalHasAnyAuthority(Authority... authorities) {
    //        return new HasAnyAuthorityPrincipalAcceptor(authorities).accept(getPrincipal());
    //    }
    //
    //    default boolean isPrincipalHasAllAuthorities(Authority... authorities) {
    //        return new HasAllAuthoritiesPrincipalAcceptor(authorities).accept(getPrincipal());
    //    }
}
