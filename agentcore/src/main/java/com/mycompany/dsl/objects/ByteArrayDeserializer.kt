package com.mycompany.dsl.objects

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.nio.charset.StandardCharsets


/**
 * @author Nikita Gorodilov
 */
class ByteArrayDeserializer: JsonDeserializer<ByteArray>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): ByteArray {
        val node = parser.codec.readTree<JsonNode>(parser)
        return node.asText().toByteArray(StandardCharsets.UTF_8)
    }
}