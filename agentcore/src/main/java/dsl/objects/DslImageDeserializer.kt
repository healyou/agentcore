package dsl.objects

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor.SignatureCodecFactory.getCodec
import com.fasterxml.jackson.core.ObjectCodec



/**
 * @author Nikita Gorodilov
 */
class DslImageDeserializer: JsonDeserializer<DslImage>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): DslImage {
        val oc = parser.getCodec()
        try {
            val node = oc.readTree<JsonNode>(parser)
            val name = node.get("name").asText()
            val data = node.get("data").asText().toByteArray()
            return DslImage(name, data)
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }
}