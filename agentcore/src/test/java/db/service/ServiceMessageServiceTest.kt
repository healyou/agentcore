package db.service

import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import objects.TypesObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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
    private lateinit var serviceMessageType: ServiceMessageType
    private val sendAgentTypeCodes = arrayListOf(
            TypesObjects.testAgentType1().code,
            TypesObjects.testAgentType2().code
    )
    private var createDate = Date(System.currentTimeMillis())
    private var useDate: Date? = null
    private var systemAgentId: Long = 0
    private var messageType: String? = UUID.randomUUID().toString()
    private var messageBodyType: String? = UUID.randomUUID().toString()

    @Before
    fun setup() {
        serviceMessageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        systemAgentId = createSystemAgent(true)

        val message = ServiceMessage(
                jsonObject,
                serviceMessageType,
                sendAgentTypeCodes,
                systemAgentId
        )
        message.createDate = createDate
        message.useDate = useDate
        message.messageType = messageType
        message.messageBodyType = messageBodyType

        id = messageService.save(message)
    }

    /* Проверка всех данных созданного сообщения */
    @Test
    fun testGetCreateMessage() {
        val message = getMessage(id!!)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(serviceMessageType.code, message.serviceMessageType.code)
        assertEquals(useDate, message.useDate)
        assertNotNull(message.createDate)
        assertEquals(false, message.isUse)
        assertEquals(systemAgentId, message.systemAgentId)
        assertEquals(messageType, message.messageType)
        assertEquals(messageBodyType, message.messageBodyType)
        assertNull(message.useDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }
    }

    /* Получение сообщения по id */
    @Test
    fun getMessageById() {
        val message = messageService.get(id!!)
        assertEquals(message.id, id)
    }

    /* Обновление параметров сообщения */
    @Test
    fun testUpdateMessage() {
        val message = getMessage(id!!)

        /* Параметры обновления */
        val newJsonObject = "{123}"
        val newServiceMessageType = messageTypeService.get(ServiceMessageType.Code.GET)
        val newSendAgentTypeCodes = arrayListOf(TypesObjects.testAgentType1().code)
        val newSystemAgentId: Long = createSystemAgent()
        val newMessageType: String? = UUID.randomUUID().toString()
        val newMessageBodyType: String? = UUID.randomUUID().toString()
        message.jsonObject = newJsonObject
        message.serviceMessageType = newServiceMessageType
        message.sendAgentTypeCodes = newSendAgentTypeCodes
        message.systemAgentId = newSystemAgentId
        message.messageType = newMessageType
        message.messageBodyType = newMessageBodyType

        /* Обновление сообщения */
        messageService.save(message)
        val updateMessage = messageService.get(message.id!!)

        /* Проверка всех обновляемых значений */
        assertEquals(newJsonObject, updateMessage.jsonObject)
        assertEquals(newServiceMessageType.code, updateMessage.serviceMessageType.code)
        assertEquals(newSystemAgentId, updateMessage.systemAgentId)
        assertEquals(newMessageType, updateMessage.messageType)
        assertEquals(newMessageBodyType, updateMessage.messageBodyType)
        newSendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue { updateMessage.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it } }
        }
    }

    /* Обновление типа сообщения */
    @Test
    fun testUpdateMessageType() {
        var message = getMessage(id!!)

        message.serviceMessageType = messageTypeService.get(ServiceMessageType.Code.GET)

        messageService.save(message)
        message = getMessage(id!!)

        assertNotEquals(serviceMessageType.code, message.serviceMessageType.code)
        assertEquals(ServiceMessageType.Code.GET.code, message.serviceMessageType.code.code)
    }

    /* Использование сообщения */
    @Test
    fun testUseMessage() {
        var message = getMessage(id!!)

        messageService.use(message)
        message = getMessage(id!!)

        assertEquals(true, message.isUse)
        assertNotNull(message.useDate)
    }

    /* Обновление системного агента */
    @Test
    fun testUpdateSystemAgent() {
        var message = getMessage(id!!)
        message.systemAgentId = createSystemAgent()

        messageService.save(message)
        message = getMessage(id!!)

        assertNotEquals(systemAgentId, message.systemAgentId)
    }

    private fun getMessage(id: Long) : ServiceMessage {
        return messageService.get(id)
    }

    private fun createSystemAgent(isSendAndGetMessages: Boolean): Long {
        return systemAgentService.create(SystemAgent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                isSendAndGetMessages
        ))
    }

    private fun createSystemAgent(): Long {
        return createSystemAgent(true)
    }
}