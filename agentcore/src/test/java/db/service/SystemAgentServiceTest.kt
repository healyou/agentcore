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
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author Nikita Gorodilov
 */
class SystemAgentServiceTest : AbstractServiceTest() {

    @Autowired
    private lateinit var systemAgentService: SystemAgentService

    /* Параметры создаваемого системного агента */
    private var id: Long? = null
    private var serviceLogin = "login"
    private var servicePassword = "password"
    private val updateDate: Date? = null
    private val isDeleted: Boolean = false
    private val isSendAndGetMessages = false

    @Before
    fun setup() {
        val systemAgent = SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages
        )
        systemAgent.isDeleted = isDeleted

        id = systemAgentService.create(systemAgent)
    }

    /* Получение созданного агента */
    @Test
    fun testGetCreateSystemAgent() {
        val systemAgent = systemAgentService.get(id!!)

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
    fun testSystemAgentScIsDeleted() {
        createAgentByIdDeletedArgs(true, false)
        val sc = SystemAgentSC()
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
    fun testSystemAgentScIsSendAndGetMessages() {
        createAgentBySendAndGetMessagesArgs(true, false)
        val sc = SystemAgentSC()
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
    fun testGetSystemAgentByServiceName() {
        val agent = systemAgentService.getByServiceLogin(serviceLogin)

        assertTrue(agent.serviceLogin == serviceLogin)
    }

    /* Нельзя создать двух агентов с одинаковый service_login */
    @Test(expected = UncategorizedSQLException::class)
    fun testCreateTwoAgentWithOneServiceName() {
        val systemAgent = SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages
        )
        systemAgentService.create(systemAgent)
    }

    /* Проверка существования агента */
    @Test()
    fun testIsExistsAgent() {
        assertTrue(systemAgentService.isExistsAgent(serviceLogin))
        assertFalse(systemAgentService.isExistsAgent(UUID.randomUUID().toString()))
    }

    private fun createAgent(isDeleted: Boolean, isSendAndGetMessages: Boolean): SystemAgent {
        val systemAgent = SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                isSendAndGetMessages
        )
        systemAgent.isDeleted = isDeleted

        return systemAgentService.get(systemAgentService.create(systemAgent))
    }

    private fun createAgentByIdDeletedArgs(vararg isDeletedArgs: Boolean) {
        isDeletedArgs.forEach {
            createAgent(it, true)
        }
    }

    private fun createAgentBySendAndGetMessagesArgs(vararg isSendAngGetMessagesArgs: Boolean) {
        isSendAngGetMessagesArgs.forEach {
            createAgent(false, it)
        }
    }
}