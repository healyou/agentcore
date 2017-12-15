package db.service

import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.objects.AgentType
import testbase.AbstractServiceTest
import java.util.*
import kotlin.test.*

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageServiceTest : AbstractServiceTest() {

    @Autowired
    private lateinit var messageService: ServiceMessageService
    @Autowired
    private lateinit var messageTypeService: ServiceMessageTypeService
    @Autowired
    private lateinit var systemAgentService: SystemAgentService

    /* Параметры создаваемого сообщения */
    private var id: Long? = null
    private var jsonObject = "{}"
    private lateinit var messageType: ServiceMessageType
    private val sendAgentTypeCodes = arrayListOf<AgentType.Code>(AgentType.Code.server, AgentType.Code.worker)
    private var createDate = Date(System.currentTimeMillis())
    private var useDate: Date? = null
    private var systemAgentId: Long = 0

    @Before
    fun setup() {
        createSystemAgent(true)
        createSystemAgent(true)
        messageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        val systemAgent = systemAgentService.get(false, true)[0]
        systemAgentId = systemAgent.id!!

        val message = ServiceMessage(
                jsonObject,
                messageType,
                sendAgentTypeCodes,
                systemAgent.id!!
        )
        message.createDate = createDate
        message.useDate = useDate

        id = messageService.save(message)
    }

    /* Получение созданного сообщения */
    @Test
    fun testGetCreateMessage() {
        val message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(messageType.code, message.messageType.code)
        assertEquals(useDate, message.useDate)
        assertNotNull(message.createDate)
        assertEquals(false, message.isUse)
        assertEquals(systemAgentId, message.systemAgentId)
        assertNull(message.useDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }
    }

    /* Обновление типа сообщения */
    @Test
    fun testUpdateMessageType() {
        var message = getMessage(id!!)

        message.messageType = messageTypeService.get(ServiceMessageType.Code.GET)

        messageService.save(message)
        message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(useDate, message.useDate)
        assertEquals(systemAgentId, message.systemAgentId)
        assertNotNull(message.createDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }

        assertNotEquals(messageType.code, message.messageType.code)
        assertEquals(ServiceMessageType.Code.GET.code, message.messageType.code.code)
    }

    /* Использование сообщения */
    @Test
    fun testUseMessage() {
        var message = getMessage(id!!)

        messageService.use(message)
        message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(messageType.code, message.messageType.code)
        assertEquals(systemAgentId, message.systemAgentId)
        assertNotNull(message.createDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }

        assertEquals(true, message.isUse)
        assertNotNull(message.useDate)
    }

    /**
     * Должно быть 2 агента для выполнения данного теста
     */
    @Test
    fun testUpdateSystemAgent() {
        var message = getMessage(id!!)

        message.systemAgentId = systemAgentService.get(false, true).filter { it.id != systemAgentId }[0].id!!

        messageService.save(message)
        message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(useDate, message.useDate)
        assertNotNull(message.createDate)
        assertEquals(false, message.isUse)
        assertEquals(messageType.code, message.messageType.code)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }

        assertNotEquals(systemAgentId, message.systemAgentId)
    }

    private fun getMessage(id: Long) : ServiceMessage {
        val messages = messageService.get(ServiceMessageSC())

        val filterMessages = messages.filter { it -> it.id!! == id }
        assertTrue {
            filterMessages.isNotEmpty() && filterMessages.size == 1
        }
        return filterMessages[0]
    }

    private fun createSystemAgent(isSendAndGetMessages: Boolean) {
        systemAgentService.create(SystemAgent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                isSendAndGetMessages
        ))
    }
}