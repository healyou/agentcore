package dsl.objects

/**
 * Класс изображение, над которым ведётся работа агента
 *
 * @author Nikita Gorodilov
 */
// TODO в отдельный класс - если работать буду с изображениями - стырить работу с файлами из EREPORT
open class DslImage(name: String, data: ByteArray) {
    var name: String? = null
    var data: ByteArray? = null

    init {
        this.name = name
        this.data = data
    }
}
