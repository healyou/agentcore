package dsl.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Класс изображение, над которым ведётся работа агента
 *
 * @author Nikita Gorodilov
 */
// TODO в отдельный класс - если работать буду с изображениями - стырить работу с файлами из EREPORT
open class DslImage(name: String, data: ByteArray) {
    @JsonProperty("name")
    var name: String? = null
    @JsonProperty("data")
    @JsonDeserialize(using = ByteArrayDeserializer::class)
    @JsonSerialize(using = ByteArraySerializer::class)
    var data: ByteArray? = null

    init {
        this.name = name
        this.data = data
    }
}
