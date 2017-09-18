package db.jdbc.servicemessage.jdbc

import agentcore.utils.Codable
import db.base.AbstractRowMapper
import db.base.toIsDeleted
import db.core.servicemessage.ServiceMessageType
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageTypeRowMapper : AbstractRowMapper<ServiceMessageType>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int): ServiceMessageType {
        return ServiceMessageType(
                getLong(rs,"id"),
                Codable.find(ServiceMessageType.Code::class.java, rs.getString("code")),
                getString(rs,"name"),
                rs.getString("is_deleted").toIsDeleted()
        )
    }
}