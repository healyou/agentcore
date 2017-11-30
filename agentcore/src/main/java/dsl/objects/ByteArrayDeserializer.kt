package dsl.objects

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode



/**
 * @author Nikita Gorodilov
 */
class ByteArrayDeserializer: JsonDeserializer<ByteArray>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): ByteArray {
        val oc = parser.getCodec()
        val node = oc.readTree<JsonNode>(parser)

        return byteArrayOf(1, 2, 3)
    }
}