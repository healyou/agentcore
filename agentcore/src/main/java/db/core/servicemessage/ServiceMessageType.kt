package db.core.servicemessage

import db.base.Codable
import db.base.IDictionary

/**
 * Тип сообщения
 *
 * @author Nikita Gorodilov
 */
class ServiceMessageType (
    override var id: Long?,
    override val code: Code,
    override val name: String,
    override val isDeleted: Boolean
): IDictionary<ServiceMessageType.Code> {

    /* Типы сообщения */
    enum class Code(override val code: String): Codable<String> {
        SEND("send"),
        GET("get");
    }
}