package db.servicemessage

import AbstractServiceTest
import agentcore.utils.Codable
import db.base.toIsDeleted
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageServiceTest : AbstractServiceTest() {

    @Autowired
    private lateinit var service: ServiceMessageService

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

        id = service.save(message)
    }

    /*  */
    @Test
    fun testGetCreateMessage() {

    }
}