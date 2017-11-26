package db.jdbc.servicemessage.jdbc

import db.base.Codable
import db.base.AbstractRowMapper
import db.base.sqlite_toAgentCodes
import db.base.sqlite_toBoolean
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageType
import service.objects.AgentType
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
                mapObjectType(rs),
                mapMessageType(rs),
                getString(rs, "send_agent_type_codes").sqlite_toAgentCodes(),
                getLong(rs, "system_agent_id")
        )

        message.id = getLong(rs, "id")
        message.senderCode = mapSenderCode(rs)
        message.createDate = getDate(rs, "create_date")
        message.useDate = getNullDate(rs, "use_date")

        return message
    }

    private fun mapObjectType(rs: ResultSet) : ServiceMessageObjectType {
        return ServiceMessageObjectType(
                getLong(rs, "object_type_id"),
                Codable.find(ServiceMessageObjectType.Code::class.java, getString(rs, "message_object_type_code")),
                getString(rs, "message_object_type_name"),
                getString(rs, "message_object_type_is_deleted").sqlite_toBoolean()
        )
    }

    private fun mapMessageType(rs: ResultSet) : ServiceMessageType {
        return ServiceMessageType(
                getLong(rs, "message_type_id"),
                Codable.find(ServiceMessageType.Code::class.java, getString(rs, "message_type_code")),
                getString(rs, "message_type_name"),
                getString(rs, "message_type_is_deleted").sqlite_toBoolean()
        )
    }

    private fun mapSenderCode(rs: ResultSet) : AgentType.Code? {
        return try {
            return Codable.find(AgentType.Code::class.java, getString(rs, "sender_code"))

        } catch (e: Exception) {
            null
        }
    }
}