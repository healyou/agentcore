package com.mycompany.db.jdbc.file.dslfile

import com.mycompany.db.core.file.dslfile.DslFileAttachment

/**
 * @author Nikita Gorodilov
 */
interface DslFileAttachmentDao {

    /**
     * Получаем текущую рабочую версию dsl файла - end_date != null
     */
    fun getDslWorkingFileBySystemAgentId(systemAgentId: Long): DslFileAttachment?

    /**
     * Получаем текущую рабочую версию dsl файла - end_date != null
     */
    fun getDslWorkingFileBySystemAgentServiceLogin(systemAgentServiceLogin: String): DslFileAttachment?

    /**
     * Проставление даты окончания работы dsl файла
     */
    fun endDslFile(dslFileId: Long)

    fun create(attachment: DslFileAttachment, systemAgentId: Long): Long
}