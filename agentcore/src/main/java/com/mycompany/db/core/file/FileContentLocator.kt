package com.mycompany.db.core.file

import com.mycompany.db.core.file.dslfile.DslFileContentRef

/**
 * Получение данных из бд
 *
 * @author Nikita Gorodilov
 */
interface FileContentLocator {

    fun getContent(ref: DslFileContentRef): FileContent
}