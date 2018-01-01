package com.mycompany

import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.form.PasswordTextField
import org.apache.wicket.markup.html.form.StatelessForm
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.apache.wicket.model.PropertyModel
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
        form.add(BootstrapFeedbackPanel("feedback"))
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
        // <!-- Bootstrap core CSS-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/bootstrap/css/bootstrap.min.css")))
        // <!-- Custom fonts for this template -->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/font-awesome/css/font-awesome.min.css")))
        // <!-- Custom styles for this template-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/css/sb-admin.css")))

        // <!-- Bootstrap core JavaScript-->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery/jquery.min.js")))
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/bootstrap/js/bootstrap.bundle.min.js")))
        // <!-- Core plugin JavaScript-->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery-easing/jquery.easing.min.js")))
    }

    private fun signedIn(session: AuthenticatedWebSession): Boolean {
        return session.isSignedIn
    }
}
