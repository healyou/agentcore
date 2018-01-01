package com.mycompany.db.core.servicemessage

import com.mycompany.db.base.Codable
import com.mycompany.db.base.IDictionary

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