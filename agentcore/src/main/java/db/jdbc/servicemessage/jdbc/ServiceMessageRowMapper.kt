package db.jdbc.servicemessage.jdbc

import db.base.Codable
import db.base.AbstractRowMapper
import db.base.sqlite_toAgentCodes
import db.base.sqlite_toBoolean
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageType
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageRowMapper : AbstractRowMapper<ServiceMessage>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int): ServiceMessage {
        val message = ServiceMessage(
                getString(rs, "json_object"),
                mapMessageType(rs),
                getLong(rs, "system_agent_id")
        )

        message.id = getLong(rs, "id")
        message.getMessageSenderCode = getNullString(rs, "sender_code")
        message.createDate = getDate(rs, "create_date")
        message.useDate = getNullDate(rs, "use_date")
        message.sendAgentTypeCodes = getNullString(rs, "send_agent_type_codes")?.sqlite_toAgentCodes()
        message.sendMessageType = getNullString(rs, "message_type")
        message.sendMessageBodyType = getNullString(rs, "message_body_type")

        return message
    }

    private fun mapMessageType(rs: ResultSet) : ServiceMessageType {
        return ServiceMessageType(
                getLong(rs, "message_type_id"),
                Codable.find(ServiceMessageType.Code::class.java, getString(rs, "message_type_code")),
                getString(rs, "message_type_name"),
                getString(rs, "message_type_is_deleted").sqlite_toBoolean()
        )
    }
}