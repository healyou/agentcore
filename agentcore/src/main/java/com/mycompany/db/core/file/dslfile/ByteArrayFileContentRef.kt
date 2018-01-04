package com.mycompany.db.core.file.dslfile

import com.mycompany.db.core.file.*

/**
 * @author Nikita Gorodilov
 */
class ByteArrayFileContentRef(private val name: String, private val data: ByteArray): FileContentRef {

    override fun getContent(visitor: FileContentLocator): FileContent {
        return ByteArrayFileContent(data)
    }

    override fun getName(): String {
        return name
    }
}