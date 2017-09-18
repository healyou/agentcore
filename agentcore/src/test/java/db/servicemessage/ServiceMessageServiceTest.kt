package db.servicemessage

import AbstractServiceTest
import agentcore.utils.Codable
import db.base.toIsDeleted
import db.core.servicemessage.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

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
    private var objectType = ServiceMessageObjectType(
            1,
            Codable.find(ServiceMessageObjectType.Code::class.java, "get_service_message"),
            "Сообщение сервиса",
            "N".toIsDeleted()
    )
    private var messageType = ServiceMessageType(
            1,
            Codable.find(ServiceMessageType.Code::class.java, "send"),
            "Отправка сообщения",
            "N".toIsDeleted()
    )
    private var createDate = Date(System.currentTimeMillis())
    private var useDate: Date? = null

    @Before
    fun setup() {
        val message = ServiceMessage(
                jsonObject,
                objectType,
                messageType
        )
        message.createDate = createDate
        message.useDate = useDate

        id = messageService.save(message)
    }

    /* Получение сообщения */
    @Test
    fun testGetCreateMessage() {

    }

    /* Создание сообщения */
    @Test
    fun testCreateMessage() {

    }

    /* Обновление типа объекта сообщения */
    @Test
    fun testUpdateMessageObjectType() {

    }

    /* Обновление типа сообщения */
    @Test
    fun testUpdateMessageType() {

    }

    /* Использование сообщения */
    @Test
    fun testUseMessage() {

    }

    /* Получение использованных сообщений */
    @Test
    fun testGetUseMessage() {

    }

    /* Получение сообщения по типу сообщения */
    @Test
    fun testGetMessageTypeMessages() {

    }
}