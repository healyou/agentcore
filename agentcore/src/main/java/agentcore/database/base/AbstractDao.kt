package agentcore.database.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author Nikita Gorodilov
 */
abstract class AbstractDao {

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate
        get
}