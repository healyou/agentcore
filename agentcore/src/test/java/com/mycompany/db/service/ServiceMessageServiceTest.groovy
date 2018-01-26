package com.mycompany.db.service

import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import objects.StringObjects
import objects.TypesObjects
import objects.initdbobjects.UserObjects
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
    private def messageBody = "{}"
    private ServiceMessageType serviceMessageType
    private def sendAgentTypeCodes = Arrays.asList(
            TypesObjects.testAgentType1().code,
            TypesObjects.testAgentType2().code
    )
    private def createDate = new Date(System.currentTimeMillis())
    private Date useDate = null
    private Long systemAgentId = 0L
    private def sendMessageType = UUID.randomUUID().toString()
    private def sendMessageBodyType = UUID.randomUUID().toString()

    @Before
    void setup() {
        serviceMessageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        systemAgentId = createSystemAgent(true)

        def message = new ServiceMessage(
                messageBody,
                serviceMessageType,
                systemAgentId
        )
        message.createDate = createDate
        message.useDate = useDate
        message.sendAgentTypeCodes = sendAgentTypeCodes
        message.sendMessageType = sendMessageType
        message.sendMessageBodyType = sendMessageBodyType

        id = messageService.save(message)
    }

    /* Проверка всех данных созданного сообщения */
    @Test
    void testGetCreateMessage() {
        def message = getMessage(id)

        /* проверка всех значений создания сообщения */
        assertEquals(id, message.id)
        assertEquals(messageBody, message.messageBody)
        assertEquals(serviceMessageType.code, message.serviceMessageType.code)
        assertEquals(useDate, message.useDate)
        assertNotNull(message.createDate)
        assertEquals(false, message.isUse())
        assertEquals(systemAgentId, message.systemAgentId)
        assertEquals(sendMessageType, message.sendMessageType)
        assertEquals(sendMessageBodyType, message.sendMessageBodyType)
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
        def newMessageBody = "{123}"
        def newServiceMessageType = messageTypeService.get(ServiceMessageType.Code.GET)
        def newSendAgentTypeCodes = Arrays.asList(TypesObjects.testAgentType1().code)
        def newSystemAgentId = createSystemAgent()
        def newMessageType = UUID.randomUUID().toString()
        def newMessageBodyType = UUID.randomUUID().toString()
        message.messageBody = newMessageBody
        message.serviceMessageType = newServiceMessageType
        message.sendAgentTypeCodes = newSendAgentTypeCodes
        message.systemAgentId = newSystemAgentId
        message.sendMessageType = newMessageType
        message.sendMessageBodyType = newMessageBodyType

        /* Обновление сообщения */
        messageService.save(message)
        def updateMessage = messageService.get(message.id)

        /* Проверка всех обновляемых значений */
        assertEquals(newMessageBody, updateMessage.messageBody)
        assertEquals(newServiceMessageType.code, updateMessage.serviceMessageType.code)
        assertEquals(newSystemAgentId, updateMessage.systemAgentId)
        assertEquals(newMessageType, updateMessage.sendMessageType)
        assertEquals(newMessageBodyType, updateMessage.sendMessageBodyType)
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

    @Test
    void "Получение последних n сообщений агента"() {
        def findSize = 5
        def agentId = createSystemAgent()
        def createMessageIds = createMessage(agentId, findSize)
        def messageList = messageService.getLastNumberItems(agentId, findSize)

        assertTrue(messageList.size() == createMessageIds.size())
        messageList.forEach {
            // проверяем id тк последние записи добавили мы
            assertTrue(createMessageIds.any { createHistoryId ->
                createHistoryId == it.id
            } && agentId == it.systemAgentId)
        }
    }

    private List<Long> createMessage(Long systemAgentId, Long size) {
        List<Long> messageIds = new ArrayList<>()
        for (i in 0..size - 1) {
            messageIds.add(createMessage(systemAgentId))
        }
        messageIds
    }

    private ServiceMessage getMessage(Long id)  {
        return messageService.get(id)
    }

    private Long createSystemAgent(Boolean isSendAndGetMessages) {
        return systemAgentService.save(new SystemAgent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                isSendAndGetMessages,
                UserObjects.testActiveUser().id,
                UserObjects.testActiveUser().id
        ))
    }

    private Long createSystemAgent() {
        return createSystemAgent(true)
    }

    private Long createMessage(Long systemAgentId) {
        def serviceMessageType = messageTypeService.get(ServiceMessageType.Code.SEND)
        def message = new ServiceMessage(
                StringObjects.randomString(),
                serviceMessageType,
                systemAgentId
        )
        messageService.save(message)
    }
}
