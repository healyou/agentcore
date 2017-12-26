package db.jdbc.file.dslfile

import db.base.AbstractDao
import db.core.file.ByteArrayFileContent
import db.core.file.FileContent
import db.core.file.dslfile.DslFileContentProvider
import db.core.file.dslfile.DslFileContentRef
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.lob.DefaultLobHandler
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
class DslFileContentProviderImpl: AbstractDao(), DslFileContentProvider {

    override fun getContent(ref: DslFileContentRef): FileContent {
        return jdbcTemplate.queryForObject(
                "SELECT data FROM dsl_file WHERE id = ?",
                RowMapper<FileContent> { rs, _ ->
                    val lobHandler = DefaultLobHandler()
                    val content = lobHandler.getBlobAsBytes(rs, "data")
                    ByteArrayFileContent(content ?: ByteArray(0))
                }, ref.getId())
    }
}