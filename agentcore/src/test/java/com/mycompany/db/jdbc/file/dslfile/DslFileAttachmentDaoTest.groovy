package com.mycompany.db.jdbc.file.dslfile

import com.mycompany.db.core.file.ByteArrayFileContent
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.FileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import objects.StringObjects
import objects.initdbobjects.AgentObjects
import org.jetbrains.annotations.NotNull
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import testbase.AbstractServiceTest

import static junit.framework.Assert.assertNotNull
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * Тестирование dao работы в dsl файлами
 *
 * @author Nikita Gorodilov
 */
class DslFileAttachmentDaoTest extends AbstractServiceTest {

    @Autowired
    DslFileAttachmentDao dslFileAttachmentDao
    @Autowired
    FileContentLocator fileContentLocator

    @Test
    void "Получение текущего экземпляра dsl файла по id агента"() {
        def agent = AgentObjects.testAgentWithManyDslAttachment()

        def dslFile = dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(agent.id)
        assertNotNull(dslFile)
        assertEquals(agent.dslFile.id, dslFile.id)
    }

    @Test
    void "Получение текущего экземпляра dsl файла по serviceLogin агента"() {
        def agent = AgentObjects.testAgentWithManyDslAttachment()

        def dslFile = dslFileAttachmentDao.getDslWorkingFileBySystemAgentServiceLogin(agent.serviceLogin)
        assertNotNull(dslFile)
        assertEquals(agent.dslFile.id, dslFile.id)
    }

    @Test
    void "Неудачное получение текущего экземпляра dsl файла по serviceLogin и id агента"() {
        def agent = AgentObjects.testAgentWithOneDslAttachment()
        def dslFile = dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(agent.id)
        dslFileAttachmentDao.endDslFile(dslFile.id)

        assertNull(dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(agent.id))
        assertNull(dslFileAttachmentDao.getDslWorkingFileBySystemAgentServiceLogin(agent.serviceLogin))
    }

    @Test
    void "Завершение работы текущего экземпляра dsl файла"() {
        def agent = AgentObjects.testAgentWithManyDslAttachment()

        dslFileAttachmentDao.endDslFile(agent.dslFile.id)
        assertNull(dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(agent.id))
    }

    @Test
    void "Создание экземпляра dsl файла"() {
        def agent = AgentObjects.testAgentWithoutDslAttachment()

        def content = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] as byte[]
        def filename = StringObjects.randomString()
        def fileSize = content.length.toLong()
        dslFileAttachmentDao.create(
                dslFileAttachment(filename, content, fileSize),
                agent.id
        )
        def dslFile = dslFileAttachmentDao.getDslWorkingFileBySystemAgentId(agent.id)

        def actualContent = dslFile.contentAsByteArray(fileContentLocator)
        assertEquals(content.length, actualContent.length)
        for (i in 0..content.length - 1) {
            assertEquals(content[i], actualContent[i])
        }
    }

    private static def dslFileAttachment(String filename, byte[] content, Long fileSize) {
        return new DslFileAttachment(
                filename,
                new FileContentRef() {
                    @Override
                    FileContent getContent(@NotNull FileContentLocator visitor) {
                        return new ByteArrayFileContent(content)
                    }
                    @Override
                    String getName() {
                        return filename
                    }
                },
                fileSize
        )
    }
}
