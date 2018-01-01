package com.mycompany.db.core.file

/**
 * Ссылка на файл в бд
 *
 * @author Nikita Gorodilov
 */
open class AbstractByIdFileContentRef(
        private val id: Long,
        private val name: String
) {

    fun getId(): Long {
        return id
    }

    fun getName(): String {
        return name
    }
}