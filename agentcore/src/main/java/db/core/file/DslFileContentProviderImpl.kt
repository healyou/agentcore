package db.core.file

import db.base.AbstractDao
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