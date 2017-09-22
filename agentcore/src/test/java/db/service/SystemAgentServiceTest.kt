package db.service

import AbstractServiceTest
import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.objects.AgentType
import java.util.*
import kotlin.test.assertEquals
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
    private val sendAgentTypeCodes = arrayListOf<AgentType.Code>(AgentType.Code.SERVER, AgentType.Code.WORKER)
    private val updateDate: Date? = null
    private val isDeleted: Boolean = false
    private val isSendAndGetMessages = false

    @Before
    fun setup() {
        val systemAgent = SystemAgent(
                serviceLogin,
                servicePassword,
                sendAgentTypeCodes,
                isSendAndGetMessages
        )

        id = systemAgentService.create(systemAgent)
    }

    /* Получение созданного сообщения */
    @Test
    fun testGetCreateSystemAgent() {
        val systemAgent = systemAgentService.get(SystemAgentSC()).filter { it.id!! == id }[0]

        /* проверка всех значений создания сообщения */
        assertEquals(id, systemAgent.id)
        assertEquals(serviceLogin, systemAgent.serviceLogin)
        assertEquals(servicePassword, systemAgent.servicePassword)
        sendAgentTypeCodes.forEach {
            systemAgent.sendAgentTypeCodes.any { new -> it == new }
        }
        assertNotNull(systemAgent.createDate)
        assertEquals(updateDate, systemAgent.updateDate)
        assertEquals(isDeleted, systemAgent.isDeleted)
        assertEquals(isSendAndGetMessages, systemAgent.isSendAndGetMessages)
    }

    /* Получение удалённых агентов */
    @Test
    fun testSystemAgentScIsDeleted() {
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
}