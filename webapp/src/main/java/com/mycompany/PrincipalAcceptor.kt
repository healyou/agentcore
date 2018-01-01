package com.mycompany

import com.mycompany.user.Principal
import java.io.Serializable

/**
 * @author Nikita Gorodilov
 */
interface PrincipalAcceptor : Serializable {

    fun accept(principal: Principal): Boolean
}
