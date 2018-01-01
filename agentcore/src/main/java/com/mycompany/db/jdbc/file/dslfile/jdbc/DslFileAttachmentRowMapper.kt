package com.mycompany.db.jdbc.file.dslfile.jdbc

import com.mycompany.db.base.AbstractRowMapper
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.file.dslfile.DslFileContentRef
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author Nikita Gorodilov
 */
class DslFileAttachmentRowMapper: AbstractRowMapper<DslFileAttachment>() {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, index: Int): DslFileAttachment {
        val attachmentId = getLong(rs, "id")
        val attachmentName = getString(rs, "filename")

        val attachment = DslFileAttachment(
                attachmentName,
                mapContent(attachmentId, attachmentName),
                getLong(rs, "length")
        )
        attachment.createDate = getDate(rs, "create_date")
        attachment.id = attachmentId

        return attachment
    }

    private fun mapContent(attachmentId: Long, attachmentName: String): DslFileContentRef {
        return DslFileContentRef(attachmentId, attachmentName)
    }
}