package com.mycompany.db.service

import com.mycompany.AbstractJdbcSpecification
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.user.User
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
                owner,
                createUser
        )
        systemAgent.dslFile = dslFile
        systemAgent.isDeleted = isDeleted

        id = systemAgentService.save(systemAgent)
    }

    def "Проверка создания dsl"() {
        setup:
        def saveAgent = systemAgentService.getById(id)
        def actualDsl = saveAgent.dslFile

        and:
        assertDslFiles(dslFile, actualDsl)
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
        systemAgent.owner.id == updateAgent.owner.id
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
        systemAgent.owner.id = newOwnerId

        when:
        systemAgentService.save(systemAgent)
        def updateAgent = systemAgentService.getById(id)
        assertDslFiles(newDslFile, systemAgent.dslFile)

        then:
        systemAgent.serviceLogin == updateAgent.serviceLogin
        systemAgent.servicePassword == updateAgent.servicePassword
        systemAgent.isDeleted == updateAgent.isDeleted
        systemAgent.isSendAndGetMessages == updateAgent.isSendAndGetMessages
        systemAgent.owner.id == updateAgent.owner.id
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
        owner.id == systemAgent.owner.id
        createUser.id == systemAgent.createUser.id
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
        def owner = UserObjects.testActiveUser()
        createAgentByOwner(owner)
        createAgentByOwner(UserObjects.testActiveUser())
        def sc = new SystemAgentSC()

        when:
        sc.ownerId = owner.id
        def agents = systemAgentService.get(sc)

        then:
        agents.stream().allMatch {
            it.owner.id == sc.ownerId
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
                UserObjects.testActiveUser(),
                UserObjects.testActiveUser()
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
        def agent = createAgentByOwner(owner)

        assertTrue(systemAgentService.isOwnAgent(agent, owner))
        assertFalse(systemAgentService.isOwnAgent(agent, notOwner))
    }

    def "Функция get(size, ownerId) корректно возвращает результат"() {
        def findSize = 5
        def owner = UserObjects.testActiveUser()
        def agentIds = createAgents(findSize, owner)
        def findAgents = systemAgentService.get(findSize, owner.id)

        assertTrue(agentIds.size() == findAgents.size())
        findAgents.forEach {
            assertTrue(ownerId == it.ownerId)
        }
    }

    def "Функция size(ownerId) корректно возвращает результат"() {
        def owner = UserObjects.testActiveUser()
        def prevCreateAgentSize = systemAgentService.size(owner.id)
        def createAgentSize = 3
        createAgents(createAgentSize, owner)

        assertEquals(prevCreateAgentSize + createAgentSize, systemAgentService.size(owner.id))
    }

    /**
     * Создание size агентов
     *
     * @param size количество создаваемых агентов
     * @return количество созданных агентов
     */
    List<Long> createAgents(Long size, User owner) {
        List<Long> agentIds = new ArrayList<>()
        for (i in 0..size - 1) {
            agentIds.add(createAgentByOwner(owner).id)
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
        return createAgent(isDeleted, isSendAndGetMessages, UserObjects.testActiveUser())
    }

    SystemAgent createAgent(Boolean isDeleted, Boolean isSendAndGetMessages, User owner) {
        def systemAgent = new SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                isSendAndGetMessages,
                owner,
                UserObjects.testActiveUser()
        )
        systemAgent.isDeleted = isDeleted

        return systemAgentService.getById(systemAgentService.save(systemAgent))
    }

    SystemAgent createAgentByOwner(User owner) {
        return createAgent(false, true, owner)
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
