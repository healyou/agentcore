package com.mycompany.objects

import com.mycompany.db.core.file.dslfile.ByteArrayFileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment

/**
 * @author Nikita Gorodilov
 */
class DslFileAttachmentObjects {

    static final def dslFileAttachment() {
        def filename = StringObjects.randomString
        def data = [1, 2, 3] as byte[]
        new DslFileAttachment(filename, new ByteArrayFileContentRef(filename, data), data.size())
    }
}
