package com.mycompany

import com.mycompany.base.WebPageSpecification

/**
 * @author Nikita Gorodilov
 */
class LoginPageSpecification extends WebPageSpecification {

    def "Страница открывается успешно"() {
        when:
        tester.startPage(LoginPage.class);

        then:
        tester.assertRenderedPage(LoginPage.class);
    }
}
