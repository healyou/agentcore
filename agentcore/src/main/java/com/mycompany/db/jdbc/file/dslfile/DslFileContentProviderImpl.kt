package com.mycompany.db.jdbc.file.dslfile

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.core.file.ByteArrayFileContent
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.dslfile.DslFileContentProvider
import com.mycompany.db.core.file.dslfile.DslFileContentRef
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
                }, ref.getId())!!
    }
}