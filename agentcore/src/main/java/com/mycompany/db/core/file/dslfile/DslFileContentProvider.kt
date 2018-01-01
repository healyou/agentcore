package com.mycompany.db.core.file.dslfile

import com.mycompany.db.core.file.FileContent

/**
 * Получение данных из бд
 *
 * @author Nikita Gorodilov
 */
interface DslFileContentProvider {

    fun getContent(ref: DslFileContentRef): FileContent
}