package db.core.file.dslfile

import db.core.file.FileContent

/**
 * Получение данных из бд
 *
 * @author Nikita Gorodilov
 */
interface DslFileContentProvider {

    fun getContent(ref: DslFileContentRef): FileContent
}