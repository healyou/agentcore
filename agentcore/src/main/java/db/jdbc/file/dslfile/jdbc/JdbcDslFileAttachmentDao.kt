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

    override fun getDslWorkingFileBySystemAgentServiceLogin(systemAgentServiceLogin: String): DslFileAttachment? {
        return try {
            jdbcTemplate.queryForObject(
                    "SELECT * FROM dsl_file WHERE end_date is null and agent_id = (SELECT id FROM system_agent WHERE service_login = ?)",
                    DslFileAttachmentRowMapper(),
                    systemAgentServiceLogin
            )
        } catch (ignored: Exception) {
            null
        }
    }

    override fun endDslFile(dslFileId: Long) {
        jdbcTemplate.update(
                "UPDATE dsl_file SET end_date = strftime('%Y-%m-%d %H:%M:%f') WHERE id = ?",
                dslFileId
        )
    }

    override fun create(attachment: DslFileAttachment, systemAgentId: Long): Long {
        jdbcTemplate.update(
                "insert into dsl_file (agent_id, filename, data, length) values (?, ?, ?, ?)",
                systemAgentId,
                attachment.filename,
                attachment.contentAsByteArray(fileContentLocator),
                attachment.fileSize
        )

        return getSequence("dsl_file")
    }
}