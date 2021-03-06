package com.mycompany

import com.mycompany.reference.*
import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.form.PasswordTextField
import org.apache.wicket.markup.html.form.StatelessForm
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.cycle.RequestCycle
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * Страница авторизации
 *
 * @author Nikita Gorodilov
 */
class LoginPage : WebPage() {

    private var login: String? = null
    private var password: String? = null
    private lateinit var feedback: BootstrapFeedbackPanel

    override fun onInitialize() {
        super.onInitialize()

        val form = object : StatelessForm<Void>("form") {

            override fun onSubmit() {
                if (AuthenticatedWebSession.get().signIn(login, password)) {
                    continueToOriginalDestination()
                    // если не было перехвата страницы, то переходим на домашнюю
                    setResponsePage(application.homePage)
                }
            }
        }
        add(form)
        feedback = BootstrapFeedbackPanel("feedback")
        form.add(feedback)
        form.add(TextField("login", PropertyModel.of<String>(this, "login")).setRequired(true))
        form.add(PasswordTextField("password", PropertyModel.of(this, "password")))
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

        response.render(BootstrapCssReference.headerItem())
        response.render(FontCssReference.headerItem())
        response.render(SbAdminCssReference.headerItem())

        response.render(JQueryJsReference.headerItem())
        response.render(BootstrapJsReference.headerItem())
    }

    private fun signedIn(session: AuthenticatedWebSession): Boolean {
        return session.isSignedIn
    }

    private fun getTarget(): AjaxRequestTarget {
        return RequestCycle.get().find(AjaxRequestTarget::class.java)
    }
}
