package com.mycompany

import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.authroles.authentication.panel.SignInPanel
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
class LoginPage : WebPage() {

    override fun onInitialize() {
        super.onInitialize()
        add(SignInPanel("signInPanel"))
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        val session = AuthenticatedWebSession.get() as SpringAuthenticatedWebSession

        if (signedIn(session)) {
            throw RestartResponseAtInterceptPageException(HomePage::class.java)
        }
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/css/style.css")))
    }

    private fun signedIn(session: AuthenticatedWebSession): Boolean {
        return session.isSignedIn
    }
}
