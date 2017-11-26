package db.jdbc.servicemessage.jdbc

import db.base.AbstractDao
import db.core.servicemessage.ServiceMessageType
import db.jdbc.servicemessage.ServiceMessageTypeDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageTypeDao : AbstractDao(), ServiceMessageTypeDao {

    override fun get(code: ServiceMessageType.Code): ServiceMessageType =
            jdbcTemplate.queryForObject("select * from service_message_type where code = ?",  ServiceMessageTypeRowMapper(), code.code)
}