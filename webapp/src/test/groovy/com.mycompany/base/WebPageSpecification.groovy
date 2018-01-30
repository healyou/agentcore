package com.mycompany.base

import com.mycompany.TestBean
import com.mycompany.db.base.Environment
import com.mycompany.db.core.user.AuthenticationService
import com.mycompany.objects.StringObjects
import com.mycompany.objects.UserObjects
import com.mycompany.user.Authority
import com.mycompany.user.Principal
import com.mycompany.user.User
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.spring.test.ApplicationContextMock
import org.apache.wicket.util.tester.WicketTester
import org.easymock.Capture
import org.easymock.IAnswer
import spock.lang.Specification

import static org.easymock.EasyMock.*

/**
 * @author Nikita Gorodilov
 */
class WebPageSpecification extends Specification {

    Set<Authority> authorities
    Principal currentUser
    WicketTester tester
    ApplicationContextMock applicationContextMock

    def setup() {
        authorities = EnumSet.allOf(Authority.class)
        applicationContextMock = new ApplicationContextMock()
        applicationContextMock.putBean(createTestBean())
        applicationContextMock.putBean(createEnvironmentBean())
        applicationContextMock.putBean(createAuthenticationServiceMock())
        tester = new WicketTester(new TestApplication(applicationContextMock))
        // todo - easymock interactions
    }

    def putBean(Object bean) {
        applicationContextMock.putBean(bean)
    }

    /**
     * Авторизация - по дефолту доступны все привелегии
     */
    protected def signIn() {
        signIn(StringObjects.randomString, StringObjects.randomString)
    }

    protected def signIn(def login, def password) {
        AuthenticatedWebSession.get().signIn(login, password)
    }

    protected void withAuthorities(Authority... authorities) {
        this.authorities = authorities
        currentUser.authorities = this.authorities
        signIn(StringObjects.randomString, StringObjects.randomString)
    }

    private AuthenticationService createAuthenticationServiceMock() {
        def authenticationService = mock(AuthenticationService.class)

        Capture<String> usernameCapture = newCapture()
        Capture<String> passwordCapture = newCapture()

        expect(authenticationService.authenticate(capture(usernameCapture), capture(passwordCapture)))
                .andStubAnswer(new IAnswer<Principal>() {
            @Override
            Principal answer() throws Throwable {
                WebPageSpecification.this.currentUser = new Principal(
                        UserObjects.loginUser(usernameCapture.value, passwordCapture.value),
                        WebPageSpecification.this.authorities
                )
                return WebPageSpecification.this.currentUser
            }
        })
        replay(authenticationService)

        authenticationService
    }

    private static Environment createEnvironmentBean() {
        return new Environment() {
            @Override
            String getProperty(String key) {
                return key
            }
        }

        /* todo снизу идут ошибка - Parameter specified as non-null is null - kotlin null-safety
        def environment = mock(Environment.class)

        Capture<String> propertyCapture = newCapture()
        expect(environment.getProperty(capture(propertyCapture)))
                .andStubAnswer(new IAnswer<String>() {
            @Override
            String answer() throws Throwable {
                return propertyCapture.value
            }
        })
        replay(environment)

        environment
        */
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
}
