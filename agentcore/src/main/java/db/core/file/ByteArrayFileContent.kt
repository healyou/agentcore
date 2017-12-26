package db.core.file

import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Контент файла в байтах
 *
 * @author Nikita Gorodilov
 */
class ByteArrayFileContent(private val content: ByteArray): FileContent {

    override fun getStream(): InputStream {
        return ByteArrayInputStream(content)
    }

    override fun getLength(): Int {
        return content.size
    }
}