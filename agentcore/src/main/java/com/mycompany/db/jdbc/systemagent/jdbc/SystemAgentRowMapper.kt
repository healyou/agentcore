package com.mycompany.db.jdbc.systemagent.jdbc

import com.mycompany.db.base.AbstractRowMapper
import com.mycompany.db.base.sqlite_toBoolean
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.jdbc.file.dslfile.DslFileAttachmentDao
import com.mycompany.user.User
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
                mapOwner(rs),
                mapCreateUser(rs)
        )
        systemAgent.id = getLong(rs, "id")
        systemAgent.createDate = getDate(rs, "create_date")
        systemAgent.isDeleted = getString(rs, "is_deleted").sqlite_toBoolean()
        systemAgent.updateDate = getNullDate(rs, "update_date")
        systemAgent.dslFile = mapDslFile(systemAgent.id!!)

        return systemAgent
    }

    private fun mapOwner(rs: ResultSet): User {
        val owner = User(getString(rs, "owner_login"), getString(rs, "owner_password"))
        owner.id = getLong(rs, "owner_id")
        owner.createDate = getDate(rs, "owner_create_date")
        owner.endDate = getNullDate(rs, "owner_end_date")
        return owner
    }

    private fun mapCreateUser(rs: ResultSet): User {
        val createUser = User(getString(rs, "create_user_login"), getString(rs, "create_user_password"))
        createUser.id = getLong(rs, "create_user_id")
        createUser.createDate = getDate(rs, "create_user_create_date")
        createUser.endDate = getNullDate(rs, "create_user_end_date")
        return createUser
    }

    private fun mapDslFile(systemAgentId: Long): DslFileAttachment? {
        return dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(systemAgentId)
    }
}