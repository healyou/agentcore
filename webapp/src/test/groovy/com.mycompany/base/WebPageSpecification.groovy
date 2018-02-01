package com.mycompany.base

import com.mycompany.db.base.Environment
import com.mycompany.db.core.user.AuthenticationService
import com.mycompany.objects.StringObjects
import com.mycompany.objects.UserObjects
import com.mycompany.user.Authority
import com.mycompany.user.Principal
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.spring.test.ApplicationContextMock
import org.apache.wicket.util.tester.WicketTester
import spock.lang.Specification

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
        applicationContextMock.putBean(createEnvironmentBean())
        applicationContextMock.putBean(createAuthenticationServiceMock())
        tester = new WicketTester(new TestApplication(applicationContextMock))
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

    protected def signOut() {
        AuthenticatedWebSession.get().signOut()
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
        Mock(AuthenticationService.class) {
            authenticate(_,_) >> {
                currentUser = new Principal(
                        UserObjects.loginUser(StringObjects.randomString, StringObjects.randomString),
                        authorities
                )
                return currentUser
            }
        }
    }

    private Environment createEnvironmentBean() {
        Mock(Environment.class) {
            getProperty(_) >> StringObjects.randomString
        }
    }
}
