package db.jdbc.systemagent.jdbc

import db.base.Codable
import db.base.AbstractRowMapper
import db.base.sqlite_toAgentCodes
import db.base.sqlite_toBoolean
import db.core.systemagent.SystemAgent
import service.objects.AgentType
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
                getString(rs, "send_agent_type_codes").sqlite_toAgentCodes(),
                getString(rs, "is_sendandget_messages").sqlite_toBoolean()
        )

        systemAgent.createDate = getDate(rs, "create_date")
        systemAgent.isDeleted = getString(rs, "is_deleted").sqlite_toBoolean()
        systemAgent.updateDate = getNullDate(rs, "update_date")
        systemAgent.id = getLong(rs, "id")

        return systemAgent
    }
}