package db.jdbc.file.dslfile.jdbc

import db.base.AbstractDao
import db.core.file.FileContentLocator
import db.core.file.dslfile.DslFileAttachment
import db.jdbc.file.dslfile.DslFileAttachmentDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
class JdbcDslFileAttachmentDao: AbstractDao(), DslFileAttachmentDao {

    @Autowired
    private lateinit var fileContentLocator: FileContentLocator

    override fun getDslWorkingFileBySystemAgentId(systemAgentId: Long): DslFileAttachment? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM dsl_file WHERE end_date is null and agent_id = ?",
                    DslFileAttachmentRowMapper(),
                    systemAgentId
            )
        } catch (ignored: Exception) {
            null
        }
    }

    override fun endDslFile(dslFileId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(attachment: DslFileAttachment): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}