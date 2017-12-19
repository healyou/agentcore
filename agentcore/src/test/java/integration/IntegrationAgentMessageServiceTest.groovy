package integration

import db.base.Environment
import objects.OtherObjects
import integration.objects.RestServiceObjects
import objects.StringObjects
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.*
import service.objects.Agent
import service.objects.MessageBodyType
import service.objects.MessageType
import testbase.AbstractServiceTest

import java.util.stream.Collectors

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
        def agent = loginWithRegistration(session)
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
        def agent = loginWithRegistration(session)
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

    @Test
    void "Регистрация агента"() {
        /* Успешная регистрация */
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))
        def agent = loginService.registration(registrationData, session)
        assertNotNull(agent)
        assertEquals(agent.name, registrationData.name)
        assertEquals(agent.masId, registrationData.masId)
        assertEquals(agent.type.code, registrationData.type)

        /* Ошибка регистрации */
        registrationData = RestServiceObjects.registrationData(StringObjects.randomString())
        assertNull(loginService.registration(registrationData, session))
    }

    @Test
    void "Авторизация и выход агента"() {
        /* Без авторизации не выйти */
        assertFalse(logout(session))

        assertNotNull(loginWithRegistration(session))
        assertTrue(logout(session))
    }

    @Test
    void "Получатель может прочитать отправленно ему сообщение"() {
        def sender = registration(session)
        def toArray = Arrays.asList(registration(session), registration(session))
        def toIds = toArray.stream().map({it.id}).collect(Collectors.toList())

        def messageTypes = getMessageTypes(session)
        def messageBodyTypes = getMessageBodyTypes(session)
        if (messageTypes == null || messageTypes.isEmpty() || messageBodyTypes == null || messageBodyTypes.isEmpty()) {
            fail("Нет типов данных для отправки сообщения")
        }

        /* Отправка сообщения */
        def messageType = messageTypes[0].code
        def messageBodyType = messageBodyTypes[0].code
        def messageBody = OtherObjects.emptyJsonObject()
        login(sender, session)
        serverMessageService.sendMessage(session, RestServiceObjects.sendMessageData(
                messageType,
                toIds,
                messageBodyType,
                messageBody
        ))
        logout(session)

        /* Чтение сообщения */
        toArray.forEach {
            assertNotNull(login(it, session))

            def messages = serverMessageService.getMessages(session, RestServiceObjects.getMessageData(sender.id))
            assertNotNull(messages)
            assertTrue(messages.size() == 1)
        }
    }

    private Agent registration(SessionManager session) {
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))
        loginService.registration(registrationData, session)
    }

    private Agent login(Agent agent, SessionManager session) {
        loginService.login(
                RestServiceObjects.loginData(agent.masId, environment.getProperty("agent.service.password")),
                session
        )
    }

    private Agent loginWithRegistration(SessionManager session) {
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))

        def outRegistrationData = loginService.registration(registrationData, session)
        def outLoginData = loginService.login(RestServiceObjects.loginData(registrationData), session)
        assertNotNull(outRegistrationData)
        assertNotNull(outLoginData)

        outLoginData
    }

    private Boolean logout(SessionManager session) {
        loginService.logout(session)
    }

    private List<MessageType> getMessageTypes(SessionManager session) {
        serverTypeService.getMessageTypes(session)
    }

    private List<MessageBodyType> getMessageBodyTypes(SessionManager session) {
        serverTypeService.getMessageBodyTypes(session)
    }
}
