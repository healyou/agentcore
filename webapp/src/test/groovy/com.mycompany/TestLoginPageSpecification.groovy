package com.mycompany

import com.mycompany.base.WicketTestApplication
import org.apache.wicket.spring.test.ApplicationContextMock
import org.apache.wicket.util.tester.WicketTester
import spock.lang.Specification

/**
 * @author Nikita Gorodilov
 */
class TestLoginPageSpecification extends Specification {

    private WicketTester tester;

    def setup() {
        ApplicationContextMock applicationContextMock = new ApplicationContextMock()
        tester = new WicketTester(new WicketTestApplication(applicationContextMock))
    }

    def "Страница открывается успешно"() {
        when:
        tester.startPage(LoginPage.class);

        then:
        tester.assertRenderedPage(LoginPage.class);
    }
}
