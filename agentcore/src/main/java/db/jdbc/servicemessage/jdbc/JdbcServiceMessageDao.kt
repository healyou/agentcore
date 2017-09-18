package db.jdbc.servicemessage.jdbc

import db.base.AbstractDao
import db.base.Utils
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessage
import db.jdbc.servicemessage.ServiceMessageDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageDao : AbstractDao(), ServiceMessageDao {

    override fun create(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "insert into service_message (json_object, object_type_id, message_type_id) values (?, ?, ?)",
                message.jsonObject,
                message.objectType.id!!,
                message.messageType.id!!
        )

        /* id последней введённой записи */
        return getSequence("service_message")
    }

    override fun update(message: ServiceMessage) : Long {
        jdbcTemplate.update(
                "update service_message set json_object = ?, object_type_id = ?, message_type_id = ? where id = ?",
                message.jsonObject,
                message.objectType.id!!,
                message.messageType.id!!,
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

    /* Делаем поисковых запрос */
    private fun applyCondition(sql: StringBuilder, sc: ServiceMessageSC) {
        val addSqlList = arrayListOf<String>()

        /* параметры запроса */
        if (Utils.isOneNotNull(
                sc.messageType,
                sc.isUse
        )) {
            sql.append(" where ")
        }
        if (sc.isUse != null) {
            val nullQuery = if (sc.isUse!!) {
                "is not null"
            } else {
                "is null"
            }
            addSqlList.add(" use_date $nullQuery) ")
        }
        if (sc.messageType != null) {
            addSqlList.add(" object_type_id = ${sc.messageType!!.id!!} ")
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