package com.mycompany

import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
abstract class AuthBasePage(parameters: PageParameters? = null) : BasePage(parameters), PrincipalSupport {

    private var modalWindow: ModalWindow? = null

    init {
        val session = AuthenticatedWebSession.get() as SpringAuthenticatedWebSession
        if (!signedIn(session)) {
            throw RestartResponseAtInterceptPageException(LoginPage::class.java)
        }
        if (!getPrincipalAcceptor().accept(getPrincipal())) {
            throw RestartResponseAtInterceptPageException(HomePage::class.java)
        }
    }

    override fun onInitialize() {
        super.onInitialize()

        modalWindow = ModalWindow("modalWindow")
        add(modalWindow!!)
    }

    abstract fun getPrincipalAcceptor(): PrincipalAcceptor

    private fun signedIn(session: AuthenticatedWebSession): Boolean {
        return session.isSignedIn
    }
}
