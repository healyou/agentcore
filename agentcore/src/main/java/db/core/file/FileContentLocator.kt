package db.core.file

/**
 * Получение данных из бд
 *
 * @author Nikita Gorodilov
 */
interface FileContentLocator {

    fun getContent(ref: DslFileContentRef): FileContent
}