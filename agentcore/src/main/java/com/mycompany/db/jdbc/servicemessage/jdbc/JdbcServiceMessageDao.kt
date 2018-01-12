package com.mycompany.db.jdbc.servicemessage.jdbc

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.base.Utils
import com.mycompany.db.base.toSqlite
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.jdbc.servicemessage.ServiceMessageDao
import com.mycompany.db.jdbc.systemagent.jdbc.SystemAgentEventHistoryRowMapper
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageDao : AbstractDao(), ServiceMessageDao {

    override fun create(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "insert into service_message (json_object, message_type_id, send_agent_type_codes, sender_code, message_type, message_body_type, system_agent_id) values (?, ?, ?, ?, ?, ?, ?)",
                message.jsonObject,
                message.serviceMessageType.id!!,
                message.sendAgentTypeCodes?.toSqlite(),
                message.getMessageSenderCode,
                message.sendMessageType,
                message.sendMessageBodyType,
                message.systemAgentId
        )

        /* id последней введённой записи */
        return getSequence("service_message")
    }

    override fun update(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "update service_message set json_object = ?, message_type_id = ?, send_agent_type_codes = ?, sender_code = ?, message_type = ?, message_body_type = ?, system_agent_id = ? where id = ?",
                message.jsonObject,
                message.serviceMessageType.id!!,
                message.sendAgentTypeCodes?.toSqlite(),
                message.getMessageSenderCode,
                message.sendMessageType,
                message.sendMessageBodyType,
                message.systemAgentId,
                message.id!!
        )

        return message.id!!
    }

    override fun use(message: ServiceMessage) {
        jdbcTemplate.update(
                "update service_message set use_date = strftime('%Y-%m-%d %H:%M:%f') where id = ?",
                message.id!!
        )
    }

    override fun get(sc: ServiceMessageSC): List<ServiceMessage> {
        val sql = StringBuilder("select * from service_message_v ")

        /* Конфигурация поискового запроса */
        applyCondition(sql, sc)

        return jdbcTemplate.query(sql.toString(), ServiceMessageRowMapper())
    }

    override fun get(id: Long): ServiceMessage {
        return jdbcTemplate.queryForObject(
                "select * from service_message_v where id = ?",
                ServiceMessageRowMapper(),
                id
        )!!
    }

    override fun getLastNumberItems(systemAgentId: Long, size: Long): List<ServiceMessage> {
        return jdbcTemplate.query(
                "select * from service_message_v where system_agent_id = ? ORDER BY create_date ASC limit 0, ?",
                ServiceMessageRowMapper(),
                systemAgentId,
                size
        )
    }

    /* Делаем поисковых запрос */
    private fun applyCondition(sql: StringBuilder, sc: ServiceMessageSC) {
        val addSqlList = arrayListOf<String>()

        /* параметры запроса */
        if (Utils.isOneNotNull(
                sc.messageType,
                sc.isUse,
                sc.systemAgentId
        )) {
            sql.append(" where ")
        }
        if (sc.isUse != null) {
            val nullQuery = if (sc.isUse!!) {
                "is not null"
            } else {
                "is null"
            }
            addSqlList.add(" use_date $nullQuery ")
        }
        if (sc.messageType != null) {
            addSqlList.add(" message_type_id = ${sc.messageType!!.id!!} ")
        }
        if (sc.systemAgentId != null) {
            addSqlList.add(" system_agent_id = ${sc.systemAgentId} ")
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