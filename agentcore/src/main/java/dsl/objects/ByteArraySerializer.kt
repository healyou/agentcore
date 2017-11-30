package dsl.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * @author Nikita Gorodilov
 */
class ByteArraySerializer: JsonSerializer<ByteArray>() {

    override fun serialize(byteArray: ByteArray, generator: JsonGenerator, provider: SerializerProvider) {
//        val definition = Definition()
//        val name = jsonParser.readValueAs(String::class.java)
//        definition.setName(name)
//        return definition
        val k = 1;
    }
}