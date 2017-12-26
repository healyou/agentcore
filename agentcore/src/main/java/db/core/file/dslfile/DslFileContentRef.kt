package db.core.file.dslfile

import db.core.file.AbstractByIdFileContentRef
import db.core.file.FileContent
import db.core.file.FileContentLocator
import db.core.file.FileContentRef

/**
 * Ссылка на данные файла в бд - посетитель получит данные, когда ему станет это надо
 *
 * @author Nikita Gorodilov
 */
class DslFileContentRef(id: Long, name: String): AbstractByIdFileContentRef(id, name), FileContentRef {

    override fun getContent(visitor: FileContentLocator): FileContent {
        return visitor.getContent(this)
    }
}