package com.mycompany.db.jdbc.systemagent.jdbc

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.base.SQLITE_NO_STRING
import com.mycompany.db.base.Utils
import com.mycompany.db.base.toSqlite
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.jdbc.file.dslfile.DslFileAttachmentDao
import com.mycompany.db.jdbc.systemagent.SystemAgentDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcSystemAgentDao : AbstractDao(), SystemAgentDao {

    @Autowired
    private lateinit var dslFileAttachmentDao: DslFileAttachmentDao

    override fun create(systemAgent: SystemAgent): Long {
        jdbcTemplate.update(
                "insert into system_agent (service_login, service_password, owner_id, create_user_id, is_sendandget_messages, is_deleted) VALUES (?, ?, ?, ?, ?, ?)",
                systemAgent.serviceLogin,
                systemAgent.servicePassword,
                systemAgent.ownerId,
                systemAgent.createUserId,
                systemAgent.isSendAndGetMessages.toSqlite(),
                systemAgent.isDeleted?.toSqlite() ?: SQLITE_NO_STRING
        )

        val agentId = getSequence("system_agent")
        if (systemAgent.dslFile != null) {
            dslFileAttachmentDao.create(systemAgent.dslFile!!, agentId)
        }

        return agentId
    }

    override fun update(systemAgent: SystemAgent): Long {
        jdbcTemplate.update(
                "update system_agent set service_login=?,service_password=?,owner_id=?,update_date=strftime('%Y-%m-%d %H:%M:%f'),is_deleted=?,is_sendandget_messages=? where id = ?",
                systemAgent.serviceLogin,
                systemAgent.servicePassword,
                systemAgent.ownerId,
                systemAgent.isDeleted?.toSqlite() ?: SQLITE_NO_STRING,
                systemAgent.isSendAndGetMessages.toSqlite(),
                systemAgent.id!!
        )
        updateAgentDslFile(systemAgent)

        return getSequence("system_agent")
    }

    override fun get(isDeleted: Boolean, isSendAndGetMessages: Boolean): List<SystemAgent> {
        return jdbcTemplate.query(
                "SELECT * FROM system_agent WHERE is_deleted = ? AND is_sendandget_messages = ?",
                SystemAgentRowMapper(dslFileAttachmentDao),
                isDeleted.toSqlite(),
                isSendAndGetMessages.toSqlite()
        )
    }

    override fun get(sc: SystemAgentSC): List<SystemAgent> {
        val sql = StringBuilder("select * from system_agent ")

        /* Конфигурация поискового запроса */
        applyCondition(sql, sc)

        return jdbcTemplate.query(sql.toString(), SystemAgentRowMapper(dslFileAttachmentDao))
    }

    override fun getById(id: Long): SystemAgent {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM system_agent WHERE id = ?",
                SystemAgentRowMapper(dslFileAttachmentDao),
                id
        )
    }

    override fun getByServiceLogin(serviceLogin: String): SystemAgent {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM system_agent WHERE service_login = ?",
                SystemAgentRowMapper(dslFileAttachmentDao),
                serviceLogin
        )
    }

    override fun isExistsAgent(serviceLogin: String): Boolean {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM system_agent WHERE service_login = ?)",
                Boolean::class.java,
                serviceLogin
        )
    }

    override fun size(): Long {
        return jdbcTemplate.queryForObject("select count(*) from system_agent", Long::class.java)
    }

    override fun get(size: Long): List<SystemAgent> {
        return jdbcTemplate.query(
                "SELECT * FROM system_agent ORDER BY create_date ASC LIMIT 0,? ",
                SystemAgentRowMapper(dslFileAttachmentDao),
                size
        )
    }

    /**
     * Обновление рабочей dsl
     */
    private fun updateAgentDslFile(systemAgent: SystemAgent) {
        val workingDsl = dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(systemAgent.id!!)
        val newDsl = systemAgent.dslFile
        /* Если была создана новая dsl для агента */
        if (newDsl != null && newDsl.isNew) {
            /* Если у агента уже была рабочая dsl */
            if (workingDsl != null) {
                dslFileAttachmentDao.endDslFile(workingDsl.id!!)
            }
            dslFileAttachmentDao.create(newDsl, systemAgent.id!!)

        } else if (workingDsl != null && newDsl == null) {
            /* Если была удалена dsl агента */
            dslFileAttachmentDao.endDslFile(workingDsl.id!!)
        }
    }

    /* Делаем поисковых запрос */
    private fun applyCondition(sql: StringBuilder, sc: SystemAgentSC) {
        val addSqlList = arrayListOf<String>()

        /* параметры запроса */
        if (Utils.isOneNotNull(
                sc.isDeleted,
                sc.isSendAndGetMessages,
                sc.ownerId
        )) {
            sql.append(" where ")
        }
        if (sc.isDeleted != null) {
            addSqlList.add(" is_deleted = '${sc.isDeleted!!.toSqlite()}' ")
        }
        if (sc.isSendAndGetMessages != null) {
            addSqlList.add(" is_sendandget_messages = '${sc.isSendAndGetMessages!!.toSqlite()}' ")
        }
        if (sc.ownerId != null) {
            addSqlList.add(" owner_id = ${sc.ownerId} ")
        }

        /* объединяем условия запроса */
        for (i in addSqlList.indices) {
            sql.append(addSqlList[i])
            if (i != addSqlList.size - 1) {
                sql.append(" and ")
            }
        }
    }
}