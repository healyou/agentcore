package com.mycompany.base

import com.mycompany.HomePage
import com.mycompany.LoginPage
import com.mycompany.MyAuthenticatedWebApplication
import com.mycompany.SpringAuthenticatedWebSession
import org.apache.wicket.Page
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import org.apache.wicket.spring.test.ApplicationContextMock

/**
 * @author Nikita Gorodilov
 */
class TestApplication extends MyAuthenticatedWebApplication {

    TestApplication(ApplicationContextMock applicationContext) {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext))
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return SpringAuthenticatedWebSession.class
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class
    }

    @Override
    Class<? extends Page> getHomePage() {
        return HomePage.class
    }
}
