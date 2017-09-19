package db.servicemessage

import AbstractServiceTest
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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

    /* Параметры создаваемого сообщения */
    private var id: Long? = null
    private var jsonObject = "{}"
    private lateinit var objectType: ServiceMessageObjectType
    private lateinit var messageType: ServiceMessageType
    private var createDate = Date(System.currentTimeMillis())
    private var useDate: Date? = null

    @Before
    fun setup() {
        messageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        objectType = messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE)

        val message = ServiceMessage(
                jsonObject,
                objectType,
                messageType
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
        assertNull(message.useDate)
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
        assertNotNull(message.createDate)

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
        assertNotNull(message.createDate)

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
        assertNotNull(message.createDate)

        assertEquals(true, message.isUse)
        assertNotNull(message.useDate)
    }

    private fun getMessage(id: Long) : ServiceMessage {
        val messages = messageService.get(ServiceMessageSC())

        val filterMessages = messages.filter { it -> it.id!! == id }
        assertTrue {
            filterMessages.isNotEmpty() && filterMessages.size == 1
        }
        return filterMessages[0]
    }
}