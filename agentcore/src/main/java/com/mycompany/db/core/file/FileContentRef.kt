package com.mycompany.db.core.file

import java.io.Serializable

/**
 * Ссылка на данные файла
 *
 * @author Nikita Gorodilov
 */
interface FileContentRef: Serializable {

    fun getContent(visitor: FileContentLocator): FileContent
    fun getName(): String
}