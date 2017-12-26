package db.core.file

import db.core.file.dslfile.DslFileContentProvider
import db.core.file.dslfile.DslFileContentRef
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Посетитель - получает файлы из бд
 *
 * @author Nikita Gorodilov
 */
@Component
class FileContentLocatorImpl(
        @Autowired
        private val dslFileContentProvider: DslFileContentProvider
): FileContentLocator {

    override fun getContent(ref: DslFileContentRef): FileContent {
        return dslFileContentProvider.getContent(ref)
    }
}