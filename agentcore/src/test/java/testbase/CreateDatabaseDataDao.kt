package testbase

import db.base.AbstractDao
import db.core.file.dslfile.DslFileContentRef
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import objects.StringObjects
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import user.Authority
import user.User
import java.util.*

/**
 * @author Nikita Gorodilov
 */
@Component
class CreateDatabaseDataDao : AbstractDao() {

    @Autowired
    private lateinit var agentService: SystemAgentService

    companion object {
        val testDskFileContentRef1Data = """
            init = {}
            onGetMessage = { _ ->}
            onLoadImage = { _ ->}
            onEndImageTask = { _ ->}
        """.trimIndent().toByteArray()
        var testDslFileContentRef1: DslFileContentRef? = null
        val testDskFileContentRef2Data = """
            init = {
                masId = ${UUID.randomUUID()}
            }
            onGetMessage = { _ ->}
            onLoadImage = { _ ->}
            onEndImageTask = { _ ->}
        """.trimIndent().toByteArray()
        var testDslFileContentRef2: DslFileContentRef? = null

        var testAgentWithOneDslAttachment: SystemAgent? = null
        /* 1 рабочая, другие старые - не используются */
        var testAgentWithManyDslAttachment: SystemAgent? = null
        var testAgentWithoutDslAttachment: SystemAgent? = null

        /* Пользователи */
        var testNotDeletedUser: User? = null
        var testDeletedUser: User? = null
    }

    /**
     * Создаём тестовые файлы
     */
    fun createData() {
        /* Агенты */
        /* Агент с 1 прикреплением */
        val agentId = createAgent()
        testDslFileContentRef1 = createDslFileContentRef(agentId, testDskFileContentRef1Data)
        /* Агент с 2 и более прикреплениями */
        val attachmentsAgentId = createAgent()
        testDslFileContentRef2 = createDslFileContentRefWithEndDate(attachmentsAgentId, testDskFileContentRef1Data)
        testDslFileContentRef2 = createDslFileContentRef(attachmentsAgentId, testDskFileContentRef2Data)
        testAgentWithOneDslAttachment = agentService.get(agentId)
        testAgentWithManyDslAttachment = agentService.get(attachmentsAgentId)
        testAgentWithoutDslAttachment = agentService.get(createAgent())

        /* Пользователи */
        testNotDeletedUser = createNotDeletedUser()
        testDeletedUser = createDeletedUser()
    }

    private fun createDeletedUser(): User {
        val user = createNotDeletedUser()
        jdbcTemplate.update("UPDATE users SET end_date = strftime('%Y-%m-%d %H:%M:%f') WHERE login = '${user.login}'")
        user.endDate = jdbcTemplate.queryForObject(
                "select end_date from users where login = ?",
                Date::class.java,
                user.login
        )
        return user
    }

    private fun createNotDeletedUser(): User {
        val login = StringObjects.randomString()
        val password = StringObjects.randomString()
        jdbcTemplate.update(
                "INSERT INTO users (login, password) VALUES (?, ?)",
                login,
                password
        )

        val user = User(login, password)
        user.id = getSequence("users")
        user.createDate = jdbcTemplate.queryForObject("select create_date from users where login = ?", Date::class.java, login)
        return user
    }

    private fun createDslFileContentRef(agentId: Long, contentRefData: ByteArray): DslFileContentRef {
        val filename = randomString()
        jdbcTemplate.update(
                "INSERT INTO dsl_file (agent_id, filename, data, length) VALUES (?, ?, ?, ?)",
                agentId,
                filename,
                contentRefData,
                contentRefData.size
        )
        return DslFileContentRef(getSequence("dsl_file"), filename)
    }

    private fun createDslFileContentRefWithEndDate(agentId: Long, contentRefData: ByteArray): DslFileContentRef {
        val filename = randomString()
        jdbcTemplate.update(
                "INSERT INTO dsl_file (agent_id, filename, data, length, end_date) VALUES (?, ?, ?, ?, strftime('%Y-%m-%d %H:%M:%f'))",
                agentId,
                filename,
                contentRefData,
                contentRefData.size
        )
        return DslFileContentRef(getSequence("dsl_file"), filename)
    }

    private fun createAgent(): Long {
        return agentService.save(SystemAgent(randomString(), randomString(), true))
    }

    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }
}