package com.mycompany

import com.mycompany.base.WebPageSpecification

/**
 * @author Nikita Gorodilov
 */
class HomePageSpecification extends WebPageSpecification {

    def setup() {
        signIn("testUserName", "testPassword")
    }

    def "Страница открывается успешно"() {
        when:
        tester.startPage(HomePage.class);

        then:
        tester.assertRenderedPage(HomePage.class);
    }
}
