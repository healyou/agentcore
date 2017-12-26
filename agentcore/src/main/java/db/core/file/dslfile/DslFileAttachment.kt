package db.core.file.dslfile

import db.core.file.Attachment
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class DslFileAttachment(
        filename: String,
        content: DslFileContentRef,
        val fileSize: Long,
        val createDate: Date
): Attachment(filename, content)