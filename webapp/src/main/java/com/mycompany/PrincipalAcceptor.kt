package com.mycompany

import java.io.Serializable

/**
 * @author Nikita Gorodilov
 */
interface PrincipalAcceptor : Serializable {

    fun accept(principal: Principal): Boolean
}
