package db.core.servicemessage

import agentcore.utils.Codable
import service.objects.Entity

/**
 * Тип объекта в сообщении
 *
 * @author Nikita Gorodilov
 */
class ServiceMessageObjectType (
        override var id: Long?,
        var code: Code,
        var name: String,
        var isDeleted: Boolean
): Entity {

    // todo для каждого MessageGoalType.Code тут сделать свой codable
    /* Типы сообщения */
    enum class Code(override val code: String): Codable<String> {
        GET_SERVICE_MESSAGE("get_service_message"),
        SEND_MESSAGE_DATA("send_message_data");
    }
}