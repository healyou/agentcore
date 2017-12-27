package db.jdbc.file.dslfile

import db.core.file.dslfile.DslFileAttachment

/**
 * @author Nikita Gorodilov
 */
interface DslFileAttachmentDao {

    /**
     * Получаем текущую рабочую версию dsl файла - end_date != null
     */
    fun getDslWorkingFileBySystemAgentId(systemAgentId: Long): DslFileAttachment?

    /**
     * Проставление даты окончания работы dsl файла
     */
    fun endDslFile(dslFileId: Long)

    fun create(attachment: DslFileAttachment, systemAgentId: Long): Long
}