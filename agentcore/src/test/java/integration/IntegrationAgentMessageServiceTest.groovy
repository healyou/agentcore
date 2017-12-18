package integration

import db.base.Environment
import objects.OtherObjects
import objects.RestServiceObjects
import objects.StringObjects
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.*
import service.objects.Agent
import testbase.AbstractServiceTest

import static org.junit.Assert.*

/**
 * Тестирование работы методов сервиса обмена сообщениями агента
 *    требуется включение сервиса
 *    тестирвуются классы пакета service(взаимодействие с restapi)
 * 
 * @author Nikita Gorodilov
 */
@Ignore
class IntegrationAgentMessageServiceTest extends AbstractServiceTest {

    @Autowired
    LoginService loginService
    @Autowired
    ServerAgentService serverAgentService
    @Autowired
    ServerMessageService serverMessageService
    @Autowired
    ServerTypeService serverTypeService
    @Autowired
    Environment environment

    def session

    @Before
    void setup() {
        session = new SessionManager()
    }

    /**
     * Тестирование лишь запуска методов
     */

    @Test
    void "Тестирование сервиса авторизации"() {
        /* без авторизации нельзя выйти */
        assertFalse(loginService.logout(session))

        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))
        assertNotNull(loginService.registration(registrationData, session))
        assertNotNull(loginService.login(RestServiceObjects.loginData(registrationData), session))
        assertTrue(loginService.logout(session))
    }

    @Test
    void "Тестирование сервиса агентов"() {
        /* без авторизации */
        assertNull(serverAgentService.getCurrentAgent(session))
        assertNull(serverAgentService.getAgents(session, RestServiceObjects.agentsData))
        assertNull(serverAgentService.getAgent(session, StringObjects.emptyString()))

        /* с авторизацией */
        def agent = login(session)
        assertNotNull(serverAgentService.getCurrentAgent(session))
        assertNotNull(serverAgentService.getAgents(session, RestServiceObjects.agentsData))
        assertNotNull(serverAgentService.getAgent(session, agent.masId))
    }

    @Test
    void "Тестирование сервиса сообщений"() {
        /* без авторизации */
        assertNull(serverMessageService.getMessages(session, RestServiceObjects.messageData))
        assertNull(serverMessageService.sendMessage(session, RestServiceObjects.randomDataSendMessageData()))

        /* с авторизацией */
        def agent = login(session)
        def messageTypes = serverTypeService.getMessageTypes(session)
        def bodyTypes = serverTypeService.getMessageBodyTypes(session)
        assertNotNull(serverMessageService.getMessages(session, RestServiceObjects.messageData))
        if (!messageTypes.isEmpty() && !bodyTypes.isEmpty()) {
            def messageType = messageTypes.get(0)
            def bodyType = bodyTypes.get(0)
            assertNotNull(serverMessageService.sendMessage(session, RestServiceObjects.sendMessageData(
                    messageType.code,
                    Collections.singletonList(agent.id),
                    bodyType.code,
                    OtherObjects.emptyJsonObject()
            )))
        }
    }

    @Test
    void "Тестирование сервиса типов данных"() {
        def messageGoalTypes
        assertNotNull(serverTypeService.getMessageBodyTypes(session))
        assertNotNull(messageGoalTypes = serverTypeService.getMessageGoalTypes(session))
        assertNotNull(serverTypeService.getMessageTypes(session))
        assertNotNull(serverTypeService.getAgentTypes(session))
        if (!messageGoalTypes.isEmpty()) {
            assertNotNull(serverTypeService.getMessageTypes(session, messageGoalTypes.get(0).code))
        }
    }

    /**
     * TODO Тестирование какой-то логики работы сервиса
     */

    /**
     * Тестирование логики работы сервиса
     */

    private Agent login(SessionManager session) {
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))

        def outRegistrationData = loginService.registration(registrationData, session)
        def outLoginData = loginService.login(RestServiceObjects.loginData(registrationData), session)
        assertNotNull(outRegistrationData)
        assertNotNull(outLoginData)

        outLoginData
    }
}
