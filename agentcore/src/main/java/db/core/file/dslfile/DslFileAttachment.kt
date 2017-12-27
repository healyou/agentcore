package db.core.file.dslfile

import com.google.common.io.ByteStreams
import db.core.file.Attachment
import db.core.file.FileContentLocator
import db.core.file.FileContentRef
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class DslFileAttachment(
        filename: String,
        content: FileContentRef,
        val fileSize: Long
): Attachment(filename, content) {

    var createDate: Date? = null

    fun contentAsByteArray(visitor: FileContentLocator): ByteArray {
        return ByteStreams.toByteArray(content.getContent(visitor).getStream())
    }
}