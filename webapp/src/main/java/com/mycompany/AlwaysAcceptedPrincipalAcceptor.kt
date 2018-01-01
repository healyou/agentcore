package com.mycompany

/**
 * @author Nikita Gorodilov
 */
class AlwaysAcceptedPrincipalAcceptor : PrincipalAcceptor {

    override fun accept(principal: Principal): Boolean {
        return true
    }
}
