package db.jdbc.user

import db.core.user.AuthenticationService
import dsl.exceptions.AuthenticationException
import objects.initdbobjects.UserObjects
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import testbase.AbstractServiceTest
import user.User

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.fail

/**
 * @author Nikita Gorodilov
 */
class AuthenticationServiceTest extends AbstractServiceTest {

    @Autowired
    AuthenticationService authenticationService;

    @Test
    void "Не удалённый пользователь авторизуется успешно"() {
        def user = UserObjects.testActiveUser()
        try {
            def principal = authenticationService.authenticate(user.login, user.password)
            assertUsers(principal.user, user)
        } catch (Exception e) {
            fail(e.message)
        }
    }

    @Test(expected = AuthenticationException.class)
    void "Удалённый пользователь не сможет авторизоваться"() {
        def user = UserObjects.testDeletedUser()
        authenticationService.authenticate(user.login, user.password)
    }

    private static def assertUsers(User expected, User actual) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.login, actual.login)
        assertEquals(expected.password, actual.password)
        assertEquals(expected.deleted, actual.deleted)
        assertEquals(expected.createDate, actual.createDate)
    }
}
