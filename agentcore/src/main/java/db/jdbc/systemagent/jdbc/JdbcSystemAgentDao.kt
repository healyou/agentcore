package db.jdbc.systemagent.jdbc

import db.base.AbstractDao
import db.base.SQLITE_NO_STRING
import db.base.Utils
import db.base.toSqlite
import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgent
import db.jdbc.file.dslfile.DslFileAttachmentDao
import db.jdbc.systemagent.SystemAgentDao
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
                "insert into system_agent (service_login, service_password, is_sendandget_messages, is_deleted) VALUES (?, ?, ?, ?)",
                systemAgent.serviceLogin,
                systemAgent.servicePassword,
                systemAgent.isSendAndGetMessages.toSqlite(),
                systemAgent.isDeleted?.toSqlite() ?: SQLITE_NO_STRING
        )

        // TODO create dsl_file

        /* id последней введённой записи */
        return getSequence("system_agent")
    }

    override fun update(systemAgent: SystemAgent): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        // todo обновление dsl записи
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

    override fun get(id: Long): SystemAgent {
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

    /* Делаем поисковых запрос */
    private fun applyCondition(sql: StringBuilder, sc: SystemAgentSC) {
        val addSqlList = arrayListOf<String>()

        /* параметры запроса */
        if (Utils.isOneNotNull(
                sc.isDeleted,
                sc.isSendAndGetMessages
        )) {
            sql.append(" where ")
        }
        if (sc.isDeleted != null) {
            addSqlList.add(" is_deleted = '${sc.isDeleted!!.toSqlite()}' ")
        }
        if (sc.isSendAndGetMessages != null) {
            addSqlList.add(" is_sendandget_messages = '${sc.isSendAndGetMessages!!.toSqlite()}' ")
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