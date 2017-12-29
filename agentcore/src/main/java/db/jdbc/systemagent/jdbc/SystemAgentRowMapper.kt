package db.jdbc.systemagent.jdbc

import db.base.AbstractRowMapper
import db.base.sqlite_toBoolean
import db.core.file.dslfile.DslFileAttachment
import db.core.systemagent.SystemAgent
import db.jdbc.file.dslfile.DslFileAttachmentDao
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class SystemAgentRowMapper(private val dslFileAttachmentDao: DslFileAttachmentDao): AbstractRowMapper<SystemAgent>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, index: Int): SystemAgent {
        val systemAgent = SystemAgent(
                getString(rs, "service_login"),
                getString(rs, "service_password"),
                getString(rs, "is_sendandget_messages").sqlite_toBoolean(),
                getLong(rs, "owner_id"),
                getLong(rs, "create_user_id")
        )
        systemAgent.id = getLong(rs, "id")
        systemAgent.createDate = getDate(rs, "create_date")
        systemAgent.isDeleted = getString(rs, "is_deleted").sqlite_toBoolean()
        systemAgent.updateDate = getNullDate(rs, "update_date")
        systemAgent.dslFile = mapDslFile(systemAgent.id!!)

        return systemAgent
    }

    private fun mapDslFile(systemAgentId: Long): DslFileAttachment? {
        return dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(systemAgentId)
    }
}