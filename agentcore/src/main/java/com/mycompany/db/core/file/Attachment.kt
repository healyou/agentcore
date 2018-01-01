package com.mycompany.db.core.file

import com.mycompany.service.objects.Entity

/**
 * Прикреплённый файл
 *
 * @author Nikita Gorodilov
 */
open class Attachment(
        val filename: String,
        val content: FileContentRef
): Entity {

    /* Идентификатор */
    override var id: Long? = null
}