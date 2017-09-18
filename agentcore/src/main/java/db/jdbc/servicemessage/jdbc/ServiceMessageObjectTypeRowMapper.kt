package db.jdbc.servicemessage.jdbc

import agentcore.utils.Codable
import db.base.AbstractRowMapper
import db.base.toIsDeleted
import db.core.servicemessage.ServiceMessageObjectType
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class ServiceMessageObjectTypeRowMapper : AbstractRowMapper<ServiceMessageObjectType>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, i: Int): ServiceMessageObjectType {
        return ServiceMessageObjectType(
                getLong(rs,"id"),
                Codable.find(ServiceMessageObjectType.Code::class.java, rs.getString("code")),
                getString(rs,"name"),
                rs.getString("is_deleted").toIsDeleted()
        )
    }
}