package db.core.servicemessage

import agentcore.utils.Codable
import service.objects.Entity

/**
 * Тип сообщения
 *
 * @author Nikita Gorodilov
 */
class ServiceMessageType (
    override var id: Long?,
    var code: Code,
    var name: String,
    var isDeleted: Boolean
): Entity {

    // todo для каждого MessageGoalType.Code тут сделать свой codable
    /* Типы сообщения */
    enum class Code(override val code: String): Codable<String> {
        SEND("send"),
        GET("get");
    }
}