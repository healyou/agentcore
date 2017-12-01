package dsl.objects

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.nio.charset.StandardCharsets

/**
 * @author Nikita Gorodilov
 */
class ByteArraySerializer: JsonSerializer<ByteArray>() {

    override fun serialize(byteArray: ByteArray, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(byteArray.toString(StandardCharsets.UTF_8))
    }
}