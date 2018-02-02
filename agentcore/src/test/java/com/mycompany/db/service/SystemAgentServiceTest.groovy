package com.mycompany.db.service

import com.mycompany.AbstractJdbcSpecification
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import objects.OtherObjects
import objects.StringObjects
import objects.initdbobjects.UserObjects
import org.springframework.beans.factory.annotation.Autowired

import static junit.framework.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * @author Nikita Gorodilov
 */
class SystemAgentServiceTest extends AbstractJdbcSpecification {

    @Autowired
    SystemAgentService systemAgentService
    @Autowired
    FileContentLocator fileContentLocator

    /* Параметры создаваемого системного агента */
    Long id = null
    def serviceLogin = "login"
    def servicePassword = "password"
    Date updateDate = null
    def isDeleted = false
    def isSendAndGetMessages = false
    def dslFileContent = [0, 1, 2] as byte[]
    def dslFilename = StringObjects.randomString()
    def dslFile = OtherObjects.dslFileAttachment(dslFilename, dslFileContent)
    def owner
    def createUser

    def setup() {
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

    def "Проверка создания dsl"() {
        setup:
        def saveAgent = systemAgentService.getById(id)
        def actualDsl = saveAgent.dslFile
        assertDslFiles(dslFile, actualDsl) // todo assert in then block
    }

    def "При сохранении объекта без изменений, данные не теряются"() {
        when:
        def systemAgent = systemAgentService.getById(id)
        systemAgentService.save(systemAgent)
        def updateAgent = systemAgentService.getById(id)
        assertDslFiles(systemAgent.dslFile, updateAgent.dslFile)

        then:
        systemAgent.serviceLogin == updateAgent.serviceLogin
        systemAgent.servicePassword == updateAgent.servicePassword
        systemAgent.isDeleted == updateAgent.isDeleted
        systemAgent.isSendAndGetMessages == updateAgent.isSendAndGetMessages
        systemAgent.ownerId == updateAgent.ownerId
    }

    def "Обновление данных агента"() {
        setup:
        def systemAgent = systemAgentService.getById(id)
        def newLogin = StringObjects.randomString()
        def newPassword = StringObjects.randomString()
        def newDslFile = OtherObjects.dslFileAttachment(StringObjects.randomString(), [0, 1, 2, 3, 4] as byte[])
        def newIsDeleted = !isDeleted
        def newIsSendAndGetMessages = !isSendAndGetMessages
        def newOwnerId = UserObjects.testDeletedUser().id

        and:
        systemAgent.serviceLogin = newLogin
        systemAgent.servicePassword = newPassword
        systemAgent.dslFile = newDslFile
        systemAgent.isDeleted = newIsDeleted
        systemAgent.isSendAndGetMessages = newIsSendAndGetMessages
        systemAgent.ownerId = newOwnerId

        when:
        systemAgentService.save(systemAgent)
        def updateAgent = systemAgentService.getById(id)
        assertDslFiles(newDslFile, systemAgent.dslFile)

        then:
        systemAgent.serviceLogin == updateAgent.serviceLogin
        systemAgent.servicePassword == updateAgent.servicePassword
        systemAgent.isDeleted == updateAgent.isDeleted
        systemAgent.isSendAndGetMessages == updateAgent.isSendAndGetMessages
        systemAgent.ownerId == updateAgent.ownerId
    }

    def "Запись нового dsl файла агента"() {
        setup:
        def newFileContent = [0, 1, 2, 3, 4] as byte[]
        def newFilename = StringObjects.randomString()
        def newDslFile = OtherObjects.dslFileAttachment(newFilename, newFileContent)

        and:
        def systemAgent = systemAgentService.getById(id)
        systemAgent.dslFile = newDslFile
        systemAgentService.save(systemAgent)

        and:
        def actualDslFile = systemAgentService.getById(id).dslFile
        assertDslFiles(newDslFile, actualDslFile)
    }

    def "Удаление рабочего dsl файла агента"() {
        setup:
        def systemAgent = systemAgentService.getById(id)

        when:
        systemAgent.dslFile = null
        systemAgentService.save(systemAgent)

        then:
        systemAgentService.getById(id).dslFile == null
    }

    def "В бд сохраняются актуальные данные агента"() {
        when:
        def systemAgent = systemAgentService.getById(id)

        then:
        /* проверка всех значений создания агента */
        id == systemAgent.id
        serviceLogin == systemAgent.serviceLogin
        servicePassword == systemAgent.servicePassword
        systemAgent.createDate != null
        systemAgent.dslFile != null
        owner.id == systemAgent.ownerId
        createUser.id == systemAgent.createUserId
        updateDate == systemAgent.updateDate
        isDeleted == systemAgent.isDeleted
        isSendAndGetMessages == systemAgent.isSendAndGetMessages
    }

    def "Получение удалённых агентов"() {
        setup:
        createAgentByIdDeletedArgs(true, false)
        def sc = new SystemAgentSC()

        when:
        sc.isDeleted = false
        def agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.isDeleted == sc.isDeleted
        }

        when:
        sc.isDeleted = true
        agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.isDeleted == sc.isDeleted
        }
    }

    def "Получение агентов для отправки сообщений"() {
        setup:
        createAgentBySendAndGetMessagesArgs(true, false)
        def sc = new SystemAgentSC()

        when:
        sc.isSendAndGetMessages = false
        def agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.isSendAndGetMessages == sc.isSendAndGetMessages
        }

        when:
        sc.isSendAndGetMessages = true
        agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.isSendAndGetMessages == sc.isSendAndGetMessages
        }
    }

    def "Получение агентов по владельцу"() {
        setup:
        def ownerId = UserObjects.testActiveUser().id
        createAgentByOwnerId(ownerId)
        createAgentByOwnerId(UserObjects.testActiveUser().id)
        def sc = new SystemAgentSC()

        when:
        sc.ownerId = ownerId
        def agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.ownerId == sc.ownerId
        }
    }

    /* Получение агента по логину в сервисе */
    def testGetSystemAgentByServiceName() {
        def agent = systemAgentService.getByServiceLogin(serviceLogin)

        assertTrue(agent.serviceLogin == serviceLogin)
    }

    //@Test(expected = UncategorizedSQLException.class)
    def "Нельзя создать двух агентов с одинаковый service_login"() {
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
    def testIsExistsAgent() {
        assertTrue(systemAgentService.isExistsAgent(serviceLogin))
        assertFalse(systemAgentService.isExistsAgent(UUID.randomUUID().toString()))
    }

    def "Функция size возвращает количество записей без ошибок"() {
        def createAgentSize = createAgents(3)
        assertTrue(systemAgentService.size() >= createAgentSize)
    }

    def "Функция get(size) возвращает size записей"() {
        def createAgentSize = createAgents(3)
        assertEquals(createAgentSize, systemAgentService.get(createAgentSize).size())
        assertEquals(1, systemAgentService.get(1).size())
    }

    def "Функция isOwnAgent корректно определяет является ли user владельцем агента"() {
        def owner = UserObjects.testActiveUser()
        def notOwner = UserObjects.testDeletedUser()
        def agent = createAgentByOwnerId(owner.id)

        assertTrue(systemAgentService.isOwnAgent(agent, owner))
        assertFalse(systemAgentService.isOwnAgent(agent, notOwner))
    }

    def "Функция get(size, ownerId) корректно возвращает результат"() {
        def findSize = 5
        def ownerId = UserObjects.testActiveUser().id
        def agentIds = createAgents(findSize, ownerId)
        def findAgents = systemAgentService.get(findSize, ownerId)

        assertTrue(agentIds.size() == findAgents.size())
        findAgents.forEach {
            assertTrue(ownerId == it.ownerId)
        }
    }

    def "Функция size(ownerId) корректно возвращает результат"() {
        def ownerId = UserObjects.testActiveUser().id
        def prevCreateAgentSize = systemAgentService.size(ownerId)
        def createAgentSize = 3
        createAgents(createAgentSize, ownerId)

        assertEquals(prevCreateAgentSize + createAgentSize, systemAgentService.size(ownerId))
    }

    /**
     * Создание size агентов
     *
     * @param size количество создаваемых агентов
     * @return количество созданных агентов
     */
    List<Long> createAgents(Long size, Long ownerId) {
        List<Long> agentIds = new ArrayList<>()
        for (i in 0..size - 1) {
            agentIds.add(createAgentByOwnerId(ownerId).id)
        }
        agentIds
    }

    /**
     * Создание size агентов
     *
     * @param size количество создаваемых агентов
     * @return количество созданных агентов
     */
    Long createAgents(Long size) {
        for (i in 0..size - 1) {
            createAgent(true, true)
        }
        size
    }

    /**
     * Сравнение двух dsl файлов на равенство
     */
    def assertDslFiles(DslFileAttachment expected, DslFileAttachment actual) {
        assert actual != null
        assert expected.filename == actual.filename
        assert expected.fileSize == actual.fileSize
        assert expected.fileSize == actual.fileSize

        def expectedData = expected.contentAsByteArray(fileContentLocator)
        def actualData = actual.contentAsByteArray(fileContentLocator)
        assert Arrays.equals(expectedData, actualData)
    }

    SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages) {
        return createAgent(isDeleted, isSendAndGetMessages, UserObjects.testActiveUser().id)
    }

    SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages, Long ownerId) {
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

    SystemAgent createAgentByOwnerId(Long ownerId) {
        return createAgent(false, true, ownerId)
    }

    def createAgentByIdDeletedArgs(Boolean... isDeletedArgs) {
        isDeletedArgs.each {
            createAgent(it, true)
        }
    }

    def createAgentBySendAndGetMessagesArgs(Boolean... isSendAngGetMessagesArgs) {
        isSendAngGetMessagesArgs.each {
            createAgent(false, it)
        }
    }
}
