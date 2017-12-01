package db.service

import testbase.AbstractServiceTest
import db.core.sc.ServiceMessageSC
import db.core.sc.SystemAgentSC
import db.core.servicemessage.*
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import objects.StringObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.objects.AgentType
import java.util.*
import kotlin.test.*

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageServiceTest : AbstractServiceTest() {

    @Autowired
    private lateinit var messageService: ServiceMessageService
    @Autowired
    private lateinit var messageObjectTypeService: ServiceMessageObjectTypeService
    @Autowired
    private lateinit var messageTypeService: ServiceMessageTypeService
    @Autowired
    private lateinit var systemAgentService: SystemAgentService

    /* Параметры создаваемого сообщения */
    private var id: Long? = null
    private var jsonObject = "{}"
    private lateinit var objectType: ServiceMessageObjectType
    private lateinit var messageType: ServiceMessageType
    private val sendAgentTypeCodes = arrayListOf<AgentType.Code>(AgentType.Code.SERVER, AgentType.Code.WORKER)
    private var createDate = Date(System.currentTimeMillis())
    private var useDate: Date? = null
    private var systemAgentId: Long = 0

    @Before
    fun setup() {
        createSystemAgent(true)
        createSystemAgent(true)
        messageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        objectType = messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE)
        val systemAgent = systemAgentService.get(false, true)[0]
        systemAgentId = systemAgent.id!!

        val message = ServiceMessage(
                jsonObject,
                objectType,
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
        assertEquals(objectType.code, message.objectType.code)
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

    /* Обновление типа объекта сообщения */
    @Test
    fun testUpdateMessageObjectType() {
        var message = getMessage(id!!)

        message.objectType = messageObjectTypeService.get(ServiceMessageObjectType.Code.SEND_MESSAGE_DATA)

        messageService.save(message)
        message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(messageType.code, message.messageType.code)
        assertEquals(useDate, message.useDate)
        assertEquals(systemAgentId, message.systemAgentId)
        assertNotNull(message.createDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }

        assertNotEquals(objectType.code, message.objectType.code)
        assertEquals(ServiceMessageObjectType.Code.SEND_MESSAGE_DATA.code, message.objectType.code.code)
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
        assertEquals(objectType.code, message.objectType.code)
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
        assertEquals(objectType.code, message.objectType.code)
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
        assertEquals(objectType.code, message.objectType.code)
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
        val id = systemAgentService.create(SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                isSendAndGetMessages
        ))
    }
}