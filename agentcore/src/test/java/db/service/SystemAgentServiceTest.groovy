package db.service

import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import objects.StringObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import testbase.AbstractServiceTest

import static junit.framework.Assert.assertEquals
import static junit.framework.TestCase.assertNotNull
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * @author Nikita Gorodilov
 */
class SystemAgentServiceTest extends AbstractServiceTest {

    @Autowired
    private SystemAgentService systemAgentService

    /* Параметры создаваемого системного агента */
    private Long id = null
    private def serviceLogin = "login"
    private def servicePassword = "password"
    private Date updateDate = null
    private def isDeleted = false
    private def isSendAndGetMessages = false

    @Before
    void setup() {
        def systemAgent = new SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages
        )
        systemAgent.isDeleted = isDeleted

        id = systemAgentService.create(systemAgent)
    }

    /* Получение созданного агента */
    @Test
    void testGetCreateSystemAgent() {
        def systemAgent = systemAgentService.get(id)

        /* проверка всех значений создания агента */
        assertEquals(id, systemAgent.id)
        assertEquals(serviceLogin, systemAgent.serviceLogin)
        assertEquals(servicePassword, systemAgent.servicePassword)
        assertNotNull(systemAgent.createDate)
        assertEquals(updateDate, systemAgent.updateDate)
        assertEquals(isDeleted, systemAgent.isDeleted)
        assertEquals(isSendAndGetMessages, systemAgent.isSendAndGetMessages)
    }

    /* Получение удалённых агентов */
    @Test
    void testSystemAgentScIsDeleted() {
        createAgentByIdDeletedArgs(true, false)
        def sc = new SystemAgentSC()
        sc.isDeleted = false

        systemAgentService.get(sc).forEach {
            assertTrue(it.isDeleted == sc.isDeleted)
        }

        sc.isDeleted = true
        systemAgentService.get(sc).forEach {
            assertTrue(it.isDeleted == sc.isDeleted)
        }
    }

    /* Получение агентов для отправки сообщений */
    @Test
    void testSystemAgentScIsSendAndGetMessages() {
        createAgentBySendAndGetMessagesArgs(true, false)
        def sc = new SystemAgentSC()
        sc.isSendAndGetMessages = false

        systemAgentService.get(sc).forEach {
            assertTrue(it.isSendAndGetMessages == sc.isSendAndGetMessages)
        }

        sc.isSendAndGetMessages = true
        systemAgentService.get(sc).forEach {
            assertTrue(it.isSendAndGetMessages == sc.isSendAndGetMessages)
        }
    }

    /* Получение агента по логину в сервисе */
    @Test
    void testGetSystemAgentByServiceName() {
        def agent = systemAgentService.getByServiceLogin(serviceLogin)

        assertTrue(agent.serviceLogin == serviceLogin)
    }

    /* Нельзя создать двух агентов с одинаковый service_login */
    @Test(expected = UncategorizedSQLException.class)
    void testCreateTwoAgentWithOneServiceName() {
        def systemAgent = new SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages
        )
        systemAgentService.create(systemAgent)
    }

    /* Проверка существования агента */
    @Test()
    void testIsExistsAgent() {
        assertTrue(systemAgentService.isExistsAgent(serviceLogin))
        assertFalse(systemAgentService.isExistsAgent(UUID.randomUUID().toString()))
    }

    private SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages) {
        def systemAgent = new SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                isSendAndGetMessages
        )
        systemAgent.isDeleted = isDeleted

        return systemAgentService.get(systemAgentService.create(systemAgent))
    }

    private def createAgentByIdDeletedArgs(Boolean... isDeletedArgs) {
        isDeletedArgs.each {
            createAgent(it, true)
        }
    }

    private def createAgentBySendAndGetMessagesArgs(Boolean... isSendAngGetMessagesArgs) {
        isSendAngGetMessagesArgs.each {
            createAgent(false, it)
        }
    }
}
