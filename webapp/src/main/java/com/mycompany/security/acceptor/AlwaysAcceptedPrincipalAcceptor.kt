package com.mycompany.security.acceptor

import com.mycompany.user.Principal

/**
 * @author Nikita Gorodilov
 */
class AlwaysAcceptedPrincipalAcceptor : PrincipalAcceptor {

    override fun accept(principal: Principal): Boolean {
        return true
    }
}
