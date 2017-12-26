package db.core.file

/**
 * Ссылка на данные файла
 *
 * @author Nikita Gorodilov
 */
interface FileContentRef {

    fun getContent(visitor: FileContentLocator): FileContent
    fun getName(): String
}