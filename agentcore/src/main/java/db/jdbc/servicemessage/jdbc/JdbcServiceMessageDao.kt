package db.jdbc.servicemessage.jdbc

import db.base.AbstractDao
import db.core.servicemessage.ServiceMessage
import db.jdbc.servicemessage.ServiceMessageDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageDao : AbstractDao(), ServiceMessageDao {

    override fun create(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "insert into service_message (json_object, object_type_id, message_type_id) values (?, ?, ?)",
                message.jsonObject,
                message.objectType.id!!,
                message.messageType.id!!
        )

        /* id последней введённой записи */
        return getSequence("service_message")
    }

    override fun update(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "update service_message set json_object = ?, object_type_id = ?, message_type_id = ? where id = ?",
                message.jsonObject,
                message.objectType.id!!,
                message.messageType.id!!,
                message.id!!
        )

        return message.id!!
    }

    override fun use(message: ServiceMessage) {
        jdbcTemplate.update(
                "update service_message set use_date = strftime('%Y-%m-%d %H:%M:%f') where id = ?",
                message.id!!
        )
    }
}