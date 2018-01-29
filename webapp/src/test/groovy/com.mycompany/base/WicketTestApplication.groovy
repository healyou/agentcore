package com.mycompany.base

import com.mycompany.*
import com.mycompany.db.base.Environment
import com.mycompany.db.core.user.AuthenticationService
import com.mycompany.user.Principal
import org.apache.wicket.Page
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import org.apache.wicket.spring.test.ApplicationContextMock
import org.easymock.Capture
import org.easymock.IAnswer

import static org.easymock.EasyMock.*

/**
 * @author Nikita Gorodilov
 */
class WicketTestApplication extends MyAuthenticatedWebApplication {

    WicketTestApplication(ApplicationContextMock applicationContext) {
        ApplicationContextMock applicationContextMock = applicationContext
        applicationContextMock.putBean(createAuthenticationServiceMock())
        applicationContextMock.putBean(createTestBean())
        applicationContextMock.putBean(createEnvironmentBean())
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContextMock))
        // todo - нет авторизации - надо как-то узнавать что мы авторизованы
        // todo - easymock interactions
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return SpringAuthenticatedWebSession
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class
    }

    @Override
    Class<? extends Page> getHomePage() {
        return HomePage.class
    }

    private static Environment createEnvironmentBean() {
        def environment = mock(Environment.class)
        replay(environment)
        environment
    }

    private static TestBean createTestBean() {
        def testBean = mock(TestBean.class)

        expect(testBean.getString())
                .andStubAnswer(new IAnswer<String>() {
            @Override
            String answer() throws Throwable {
                "123"
            }
        })
        expect(testBean.getString2()).andStubReturn("123")
        replay(testBean)

        testBean
    }

    private static AuthenticationService createAuthenticationServiceMock() {
        def authenticationService = mock(AuthenticationService.class)

        Capture<String> stringCapture = newCapture()

        expect(authenticationService.authenticate(capture(stringCapture), capture(stringCapture)))
                .andStubAnswer(new IAnswer<Principal>() {
            @Override
            Principal answer() throws Throwable {
                new Principal(null, null)
            }
        })
        replay(authenticationService)

        authenticationService
    }
}
