package com.mycompany.db.jdbc.servicemessage.jdbc

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.jdbc.servicemessage.ServiceMessageTypeDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageTypeDao : AbstractDao(), ServiceMessageTypeDao {

    override fun get(code: ServiceMessageType.Code): ServiceMessageType =
            jdbcTemplate.queryForObject("select * from service_message_type where code = ?",  ServiceMessageTypeRowMapper(), code.code)
}