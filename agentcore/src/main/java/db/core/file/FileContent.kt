package db.core.file

import java.io.InputStream

/**
 * Данные файла
 *
 * @author Nikita Gorodilov
 */
interface FileContent {

    fun getStream(): InputStream
    fun getLength(): Int
}