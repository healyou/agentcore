package db.jdbc.servicemessage.jdbc

import db.base.AbstractDao
import db.core.servicemessage.ServiceMessageObjectType
import db.jdbc.servicemessage.ServiceMessageObjectTypeDao
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class JdbcServiceMessageObjectTypeDao : AbstractDao(), ServiceMessageObjectTypeDao {

    // TODO общий класс для dictionary
    
    override fun get(code: ServiceMessageObjectType.Code): ServiceMessageObjectType =
            jdbcTemplate.queryForObject("select * from service_message_object_type where code = ?",  ServiceMessageObjectTypeRowMapper(), code.code)
}