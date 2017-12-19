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

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNull
import static junit.framework.Assert.assertTrue
import static junit.framework.TestCase.assertNotNull
import static org.junit.Assert.assertNotEquals

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageServiceTest extends AbstractServiceTest {

    @Autowired
    private ServiceMessageService messageService
    @Autowired
    private ServiceMessageTypeService messageTypeService
    @Autowired
    private SystemAgentService systemAgentService

    /* Параметры создаваемого сообщения */
    private Long id = null
    private def jsonObject = "{}"
    private ServiceMessageType serviceMessageType
    private def sendAgentTypeCodes = Arrays.asList(
            TypesObjects.testAgentType1().code,
            TypesObjects.testAgentType2().code
    )
    private def createDate = new Date(System.currentTimeMillis())
    private Date useDate = null
    private Long systemAgentId = 0L
    private def messageType = UUID.randomUUID().toString()
    private def messageBodyType = UUID.randomUUID().toString()

    @Before
    void setup() {
        serviceMessageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        systemAgentId = createSystemAgent(true)

        def message = new ServiceMessage(
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
    void testGetCreateMessage() {
        def message = getMessage(id)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(jsonObject, message.jsonObject)
        assertEquals(serviceMessageType.code, message.serviceMessageType.code)
        assertEquals(useDate, message.useDate)
        assertNotNull(message.createDate)
        assertEquals(false, message.isUse())
        assertEquals(systemAgentId, message.systemAgentId)
        assertEquals(messageType, message.messageType)
        assertEquals(messageBodyType, message.messageBodyType)
        assertNull(message.useDate)
        sendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue(message.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it })
        }
    }

    /* Получение сообщения по id */
    @Test
    void getMessageById() {
        def message = messageService.get(id)
        assertEquals(message.id, id)
    }

    /* Обновление параметров сообщения */
    @Test
    void testUpdateMessage() {
        def message = getMessage(id)

        /* Параметры обновления */
        def newJsonObject = "{123}"
        def newServiceMessageType = messageTypeService.get(ServiceMessageType.Code.GET)
        def newSendAgentTypeCodes = Arrays.asList(TypesObjects.testAgentType1().code)
        def newSystemAgentId = createSystemAgent()
        def newMessageType = UUID.randomUUID().toString()
        def newMessageBodyType = UUID.randomUUID().toString()
        message.jsonObject = newJsonObject
        message.serviceMessageType = newServiceMessageType
        message.sendAgentTypeCodes = newSendAgentTypeCodes
        message.systemAgentId = newSystemAgentId
        message.messageType = newMessageType
        message.messageBodyType = newMessageBodyType

        /* Обновление сообщения */
        messageService.save(message)
        def updateMessage = messageService.get(message.id)

        /* Проверка всех обновляемых значений */
        assertEquals(newJsonObject, updateMessage.jsonObject)
        assertEquals(newServiceMessageType.code, updateMessage.serviceMessageType.code)
        assertEquals(newSystemAgentId, updateMessage.systemAgentId)
        assertEquals(newMessageType, updateMessage.messageType)
        assertEquals(newMessageBodyType, updateMessage.messageBodyType)
        newSendAgentTypeCodes.forEach {  itGetMessageTypeCode ->
            assertTrue(updateMessage.sendAgentTypeCodes.stream().anyMatch { itGetMessageTypeCode == it })
        }
    }

    /* Обновление типа сообщения */
    @Test
    void testUpdateMessageType() {
        def message = getMessage(id)

        message.serviceMessageType = messageTypeService.get(ServiceMessageType.Code.GET)

        messageService.save(message)
        message = getMessage(id)

        assertNotEquals(serviceMessageType.code, message.serviceMessageType.code)
        assertEquals(ServiceMessageType.Code.GET.code, message.serviceMessageType.code.code)
    }

    /* Использование сообщения */
    @Test
    void testUseMessage() {
        def message = getMessage(id)

        messageService.use(message)
        message = getMessage(id)

        assertEquals(true, message.isUse())
        assertNotNull(message.useDate)
    }

    /* Обновление системного агента */
    @Test
    void testUpdateSystemAgent() {
        def message = getMessage(id)
        message.systemAgentId = createSystemAgent()

        messageService.save(message)
        message = getMessage(id)

        assertNotEquals(systemAgentId, message.systemAgentId)
    }

    private ServiceMessage getMessage(Long id)  {
        return messageService.get(id)
    }

    private Long createSystemAgent(Boolean isSendAndGetMessages) {
        return systemAgentService.create(new SystemAgent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                isSendAndGetMessages
        ))
    }

    private Long createSystemAgent() {
        return createSystemAgent(true)
    }
}
