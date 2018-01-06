package com.mycompany.db.service

import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import objects.OtherObjects
import objects.StringObjects
import objects.initdbobjects.UserObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import testbase.AbstractServiceTest

import static junit.framework.Assert.assertEquals
import static junit.framework.TestCase.assertNotNull
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

/**
 * @author Nikita Gorodilov
 */
class SystemAgentServiceTest extends AbstractServiceTest {

    @Autowired
    private SystemAgentService systemAgentService
    @Autowired
    private FileContentLocator fileContentLocator

    /* Параметры создаваемого системного агента */
    private Long id = null
    private def serviceLogin = "login"
    private def servicePassword = "password"
    private Date updateDate = null
    private def isDeleted = false
    private def isSendAndGetMessages = false
    private def dslFileContent = [0, 1, 2] as byte[]
    private def dslFilename = StringObjects.randomString()
    private def dslFile = OtherObjects.dslFileAttachment(dslFilename, dslFileContent)
    private def owner
    private def createUser

    @Before
    void setup() {
        /* Пользователи создаются в @Before родителя */
        owner = UserObjects.testActiveUser()
        createUser = UserObjects.testActiveUser()

        def systemAgent = new SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages,
                owner.id,
                createUser.id
        )
        systemAgent.dslFile = dslFile
        systemAgent.isDeleted = isDeleted

        id = systemAgentService.save(systemAgent)
    }

    @Test
    void "Проверка создания dsl"() {
        def saveAgent = systemAgentService.getById(id)
        def actualDsl = saveAgent.dslFile
        assertDslFiles(dslFile, actualDsl)
    }

    @Test
    void "При сохранении объекта без изменений, данные не теряются"() {
        def systemAgent = systemAgentService.getById(id)

        systemAgentService.save(systemAgent)
        def updateAgent = systemAgentService.getById(id)
        assertEquals(systemAgent.serviceLogin, updateAgent.serviceLogin)
        assertEquals(systemAgent.servicePassword, updateAgent.servicePassword)
        assertDslFiles(systemAgent.dslFile, updateAgent.dslFile)
        assertEquals(systemAgent.isDeleted, updateAgent.isDeleted)
        assertEquals(systemAgent.isSendAndGetMessages, updateAgent.isSendAndGetMessages)
        assertEquals(systemAgent.ownerId, updateAgent.ownerId)
    }

    @Test
    void "Обновление данных агента"() {
        def systemAgent = systemAgentService.getById(id)

        def newLogin = StringObjects.randomString()
        def newPassword = StringObjects.randomString()
        def newDslFile = OtherObjects.dslFileAttachment(StringObjects.randomString(), [0, 1, 2, 3, 4] as byte[])
        def newIsDeleted = !isDeleted
        def newIsSendAndGetMessages = !isSendAndGetMessages
        def newOwnerId = UserObjects.testDeletedUser().id

        systemAgent.serviceLogin = newLogin
        systemAgent.servicePassword = newPassword
        systemAgent.dslFile = newDslFile
        systemAgent.isDeleted = newIsDeleted
        systemAgent.isSendAndGetMessages = newIsSendAndGetMessages
        systemAgent.ownerId = newOwnerId

        systemAgentService.save(systemAgent)
        def updateAgent = systemAgentService.getById(id)
        assertEquals(newLogin, updateAgent.serviceLogin)
        assertEquals(newPassword, updateAgent.servicePassword)
        assertDslFiles(newDslFile, systemAgent.dslFile)
        assertEquals(newIsDeleted, updateAgent.isDeleted)
        assertEquals(newIsSendAndGetMessages, updateAgent.isSendAndGetMessages)
        assertEquals(newOwnerId, systemAgent.ownerId)
    }

    @Test
    void "Запись нового dsl файла агента"() {
        def newFileContent = [0, 1, 2, 3, 4] as byte[]
        def newFilename = StringObjects.randomString()
        def newDslFile = OtherObjects.dslFileAttachment(newFilename, newFileContent)

        def systemAgent = systemAgentService.getById(id)
        systemAgent.dslFile = newDslFile
        systemAgentService.save(systemAgent)

        def actualDslFile = systemAgentService.getById(id).dslFile
        assertDslFiles(newDslFile, actualDslFile)
    }

    @Test
    void "Удаление рабочего dsl файла агента"() {
        def systemAgent = systemAgentService.getById(id)
        systemAgent.dslFile = null
        systemAgentService.save(systemAgent)

        assertNull(systemAgentService.getById(id).dslFile)
    }

    @Test
    void "В бд сохраняются актуальные данные агента"() {
        def systemAgent = systemAgentService.getById(id)

        /* проверка всех значений создания агента */
        assertEquals(id, systemAgent.id)
        assertEquals(serviceLogin, systemAgent.serviceLogin)
        assertEquals(servicePassword, systemAgent.servicePassword)
        assertNotNull(systemAgent.createDate)
        assertNotNull(systemAgent.dslFile)
        assertEquals(owner.id, systemAgent.ownerId)
        assertEquals(createUser.id, systemAgent.createUserId)
        assertEquals(updateDate, systemAgent.updateDate)
        assertEquals(isDeleted, systemAgent.isDeleted)
        assertEquals(isSendAndGetMessages, systemAgent.isSendAndGetMessages)
    }

    @Test
    void "Получение удалённых агентов"() {
        createAgentByIdDeletedArgs(true, false)
        def sc = new SystemAgentSC()
        sc.isDeleted = false

        systemAgentService.get(sc).forEach {
            assertTrue(it.isDeleted == sc.isDeleted)
        }

        sc.isDeleted = true
        systemAgentService.get(sc).forEach {
            assertTrue(it.isDeleted == sc.isDeleted)
        }
    }

    @Test
    void "Получение агентов для отправки сообщений"() {
        createAgentBySendAndGetMessagesArgs(true, false)
        def sc = new SystemAgentSC()
        sc.isSendAndGetMessages = false

        systemAgentService.get(sc).forEach {
            assertTrue(it.isSendAndGetMessages == sc.isSendAndGetMessages)
        }

        sc.isSendAndGetMessages = true
        systemAgentService.get(sc).forEach {
            assertTrue(it.isSendAndGetMessages == sc.isSendAndGetMessages)
        }
    }

    @Test
    void "Получение агентов по владельцу"() {
        def ownerId = UserObjects.testActiveUser().id
        createAgentByOwnerId(ownerId)
        createAgentByOwnerId(UserObjects.testActiveUser().id)
        def sc = new SystemAgentSC()
        sc.ownerId = ownerId

        systemAgentService.get(sc).forEach {
            assertTrue(it.ownerId == sc.ownerId)
        }
    }

    /* Получение агента по логину в сервисе */
    @Test
    void testGetSystemAgentByServiceName() {
        def agent = systemAgentService.getByServiceLogin(serviceLogin)

        assertTrue(agent.serviceLogin == serviceLogin)
    }

    @Test(expected = UncategorizedSQLException.class)
    void "Нельзя создать двух агентов с одинаковый service_login"() {
        def systemAgent = new SystemAgent(
                serviceLogin,
                servicePassword,
                isSendAndGetMessages,
                UserObjects.testActiveUser().id,
                UserObjects.testActiveUser().id
        )
        systemAgentService.save(systemAgent)
    }

    /* Проверка существования агента */
    @Test()
    void testIsExistsAgent() {
        assertTrue(systemAgentService.isExistsAgent(serviceLogin))
        assertFalse(systemAgentService.isExistsAgent(UUID.randomUUID().toString()))
    }

    @Test
    void "Функция size возвращает количество записей без ошибок"() {
        def createAgentSize = createAgents(3)
        assertTrue(systemAgentService.size() >= createAgentSize)
    }

    @Test
    void "Функция get(size) возвращает size записей"() {
        def createAgentSize = createAgents(3)
        assertEquals(createAgentSize, systemAgentService.get(createAgentSize).size())
        assertEquals(1, systemAgentService.get(1).size())
    }

    @Test
    void "Функция isOwnAgent корректно определяет является ли user владельцем агента"() {
        def owner = UserObjects.testActiveUser()
        def notOwner = UserObjects.testDeletedUser()
        def agent = createAgentByOwnerId(owner.id)

        assertTrue(systemAgentService.isOwnAgent(agent, owner))
        assertFalse(systemAgentService.isOwnAgent(agent, notOwner))
    }

    /**
     * Создание size агентов
     *
     * @param size количество создаваемых агентов
     * @return количество созданных агентов
     */
    private Long createAgents(Long size) {
        for (i in 0..size - 1) {
            createAgent(true, true)
        }
        size
    }

    /**
     * Сравнение двух dsl файлов на равенство
     */
    private def assertDslFiles(DslFileAttachment expected, DslFileAttachment actual) {
        assertNotNull(actual)
        assertEquals(expected.filename, actual.filename)
        assertEquals(expected.fileSize, actual.fileSize)
        assertEquals(expected.fileSize, actual.fileSize)

        def expectedData = expected.contentAsByteArray(fileContentLocator)
        def actualData = actual.contentAsByteArray(fileContentLocator)
        for (i in 0..expectedData.length - 1) {
            assertEquals(expectedData[i], actualData[i])
        }
    }

    private SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages) {
        return createAgent(isDeleted, isSendAndGetMessages, UserObjects.testActiveUser().id)
    }

    private SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages, Long ownerId) {
        def systemAgent = new SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                isSendAndGetMessages,
                ownerId,
                UserObjects.testActiveUser().id
        )
        systemAgent.isDeleted = isDeleted

        return systemAgentService.getById(systemAgentService.save(systemAgent))
    }

    private def createAgentByOwnerId(Long ownerId) {
        return createAgent(false, true, ownerId)
    }

    private def createAgentByIdDeletedArgs(Boolean... isDeletedArgs) {
        isDeletedArgs.each {
            createAgent(it, true)
        }
    }

    private def createAgentBySendAndGetMessagesArgs(Boolean... isSendAngGetMessagesArgs) {
        isSendAngGetMessagesArgs.each {
            createAgent(false, it)
        }
    }
}
