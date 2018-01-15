package com.mycompany.dsl.objects

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.Serializable

/**
 * Класс изображение, над которым ведётся работа агента
 *
 * @author Nikita Gorodilov
 */
open class DslImage(
        @JsonProperty("name")
        var name: String? = null,
        @JsonProperty("data")
        @JsonSerialize(using = ByteArraySerializer::class)
        @JsonDeserialize(using = ByteArrayDeserializer::class)
        var data: ByteArray? = null
): Serializable
