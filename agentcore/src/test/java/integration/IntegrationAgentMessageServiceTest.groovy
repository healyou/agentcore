package integration

import com.mycompany.db.base.Environment
import com.mycompany.service.*
import com.mycompany.service.objects.*
import integration.objects.RestServiceObjects
import objects.OtherObjects
import objects.StringObjects
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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
        def toAgents = Arrays.asList(registration(session), registration(session))
        sendMessage(session, sender, toAgents)

        /* Чтение сообщения */
        toAgents.forEach {
            assertGetMessagesSize(session, it, sender.id, false, 1)
        }
    }

    @Test
    void "Получатель не может прочитать одно и то же сообщение дважды"() {
        def sender = registration(session)
        def toAgents = Arrays.asList(registration(session))
        sendMessage(session, sender, toAgents)

        toAgents.forEach {
            assertGetMessagesSize(session, it, sender.id, false, 1)
            assertGetMessagesSize(session, it, sender.id, false, 0)
        }
    }

    @Test
    void "Чтение прочитанного сообщения"() {
        def sender = registration(session)
        def toAgents = Arrays.asList(registration(session))
        sendMessage(session, sender, toAgents)

        toAgents.forEach {
            assertGetMessagesSize(session, it, sender.id, false, 1)
            assertGetMessagesSize(session, it, sender.id, true, 1)
        }
    }

    /**
     * Сервис работы с агентами
     */

    @Test
    void "Получение текущего агента"() {
        def agent = registration(session)
        login(agent, session)
        def retAgent = serverAgentService.getCurrentAgent(session)

        assertNotNull(retAgent)
        assertEqualsAgentParams(agent, retAgent)
    }

    @Test
    void "Получение агента по masId"() {
        def agent = registration(session)
        login(agent, session)
        def retAgent = serverAgentService.getAgent(session, agent.masId)

        assertNotNull(retAgent)
        assertEqualsAgentParams(agent, retAgent)
    }

    @Test
    void "Получение агентов по типу"() {
        def agent = registration(session)
        login(agent, session)
        def retAgents = serverAgentService.getAgents(session, RestServiceObjects.getAgentsDataWithType(agent.type.code))

        assertTrue(retAgents != null && !retAgents.isEmpty())
        retAgents.each {
            assertEquals(it.type.code, agent.type.code)
        }
    }

    @Test
    void "Получение агентов по имени"() {
        def agent = registration(session)
        login(agent, session)
        def retAgents = serverAgentService.getAgents(session, RestServiceObjects.getAgentsDataWithName(agent.name))

        assertTrue(retAgents != null && !retAgents.isEmpty())
        retAgents.each {
            assertEquals(it.name, agent.name)
        }
    }

    @Test
    void "Получение не удалённых агентов"() {
        def agent = registration(session)
        login(agent, session)
        def retAgents = serverAgentService.getAgents(session, RestServiceObjects.getAgentsDataWithIsDeleted(false))

        assertTrue(retAgents != null && !retAgents.isEmpty())
        retAgents.each {
            assertFalse(it.deleted)
        }
    }

    /**
     * Логин сервис
     */

    @Test
    void "Данные при успешном логине совпадают с регистрационными данными"() {
        def agent = registration(session)
        def retAgent = login(agent, session)

        assertNotNull(retAgent)
        assertEqualsAgentParams(agent, retAgent)
    }

    @Test
    void "Данные метода регистрации совпадают регистрационными данными"() {
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))
        def agent = registration(session, registrationData)

        assertNotNull(agent)
        assertEquals(registrationData.name, agent.name)
        assertEquals(registrationData.type, agent.type.code)
        assertEquals(registrationData.masId, agent.masId)
    }

    private void assertEqualsAgentParams(Agent first, Agent two) {
        assertEquals(first.id, two.id)
        assertEquals(first.createDate, two.createDate)
        assertEquals(first.deleted, two.deleted)
        assertEquals(first.masId, two.masId)
        assertEquals(first.name, two.name)
        assertEquals(first.type.code, two.type.code)
    }

    private void assertGetMessagesSize(SessionManager session, Agent recipient,Long senderId, boolean isViewed, int size) {
        assertNotNull(login(recipient, session))
        def messages = getMessages(session, senderId, isViewed)
        assertNotNull(messages)
        assertTrue(messages.size() == size)
    }

    private void sendMessage(SessionManager session, Agent sender, List<Agent> toAgents) {
        def toAgentsIds = toAgents.stream().map({it.id}).collect(Collectors.toList())

        def messageTypes = getMessageTypes(session)
        def messageBodyTypes = getMessageBodyTypes(session)
        assertNotNullAndNotEmpty(messageTypes, messageBodyTypes)

        /* Отправка сообщения */
        def messageType = messageTypes[0].code
        def messageBodyType = messageBodyTypes[0].code
        def messageBody = OtherObjects.emptyJsonObject()
        loginAndSendMessage(messageType, toAgentsIds, messageBodyType, messageBody, sender, session)
        logout(session)
    }

    private List<Message> getMessages(SessionManager session, Long senderId, Boolean isViewed) {
        serverMessageService.getMessages(session, RestServiceObjects.getMessageData(senderId, isViewed))
    }

    private void loginAndSendMessage(String messageType, List<Long> recipientsIds, String messageBodyType, String messageBody,
                                     Agent sender, SessionManager session) {
        login(sender, session)
        sendMessage(messageType, recipientsIds, messageBodyType, messageBody, session)
    }

    private void sendMessage(String messageType, List<Long> recipientsIds, String messageBodyType, String messageBody,
                             SessionManager session) {
        serverMessageService.sendMessage(session, RestServiceObjects.sendMessageData(
                messageType,
                recipientsIds,
                messageBodyType,
                messageBody
        ))
    }

    private void assertNotNullAndNotEmpty(List... params) {
        params.each {
            assertTrue(it != null && !it.isEmpty())
        }
    }

    private Agent registration(SessionManager session) {
        def registrationData = RestServiceObjects.registrationData(environment.getProperty("agent.service.password"))
        loginService.registration(registrationData, session)
    }

    private Agent registration(SessionManager session, RegistrationData registrationData) {
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
