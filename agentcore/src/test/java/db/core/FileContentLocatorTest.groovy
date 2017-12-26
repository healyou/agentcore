package db.core

import com.google.common.io.ByteStreams
import db.core.file.FileContentLocator
import objects.initdbobjects.FilesObjects
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import testbase.AbstractServiceTest

import static org.junit.Assert.assertEquals

/**
 * @author Nikita Gorodilov
 */
class FileContentLocatorTest extends AbstractServiceTest {

    @Autowired
    FileContentLocator fileContentLocator

    @Test
    void "Получение данных DslFileContent"() {
        def content = fileContentLocator.getContent(FilesObjects.testDslFile1ContentRef())
        def contentExpectedData = FilesObjects.testDslFile1Data()

        byte[] bytes = ByteStreams.toByteArray(content.stream)
        assertEquals(contentExpectedData.size(), content.stream.available())
        assertEquals(contentExpectedData.size(), content.length)
        for (i in 0..bytes.length - 1) {
            assertEquals(contentExpectedData[i], bytes[i])
        }
    }
}
