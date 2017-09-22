package db.jdbc.systemagent.jdbc

import db.base.AbstractRowMapper
import db.base.toIsDeleted
import db.core.systemagent.SystemAgent
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class SystemAgentRowMapper : AbstractRowMapper<SystemAgent>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, index: Int): SystemAgent {
        val systemAgent = SystemAgent(
                getString(rs, "service_login"),
                getString(rs, "service_password"),
                getString(rs, "send_agent_type_codes"),
                getBoolean(rs, "is_sendandget_messages")
        )

        systemAgent.createDate = getDate(rs, "create_date")
        systemAgent.isDeleted = getString(rs, "is_deleted").toIsDeleted()
        systemAgent.updateDate = getNullDate(rs, "update_date")
        systemAgent.id = getLong(rs, "id")

        return systemAgent
    }
}