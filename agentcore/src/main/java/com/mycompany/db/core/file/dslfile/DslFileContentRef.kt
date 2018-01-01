package com.mycompany.db.core.file.dslfile

import com.mycompany.db.core.file.AbstractByIdFileContentRef
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.FileContentRef

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