package db.core.file

/**
 * Получение данных из бд
 *
 * @author Nikita Gorodilov
 */
interface DslFileContentProvider {

    fun getContent(ref: DslFileContentRef): FileContent
}