package testbase

import com.mycompany.db.base.AbstractDao
import com.mycompany.db.core.file.dslfile.DslFileContentRef
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.mycompany.user.User
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
            init = {
                type = "type"
                name = "a1_testdsl"
                masId = "a1_testdsl"
                defaultBodyType = "json"
                localMessageTypes = ["test_event1"]
                taskTypes = ["test_task_type1"]
            }
            onGetServiceMessage = { _ ->}
            onGetLocalMessage = { _->}
            onLoadImage = { _ ->}
            onEndImageTask = { _ ->}
        """.trimIndent().toByteArray()
        var testDslFileContentRef1: DslFileContentRef? = null
        val testDskFileContentRef2Data = """
            init = {
                masId = ${UUID.randomUUID()}
            }
            onGetServiceMessage = { _ ->}
            onGetLocalMessage = { _->}
            onLoadImage = { _ ->}
            onEndImageTask = { _ ->}
        """.trimIndent().toByteArray()
        var testDslFileContentRef2: DslFileContentRef? = null

        var testAgentWithOneDslAttachment: SystemAgent? = null
        /* 1 рабочая, другие старые - не используются */
        var testAgentWithManyDslAttachment: SystemAgent? = null
        var testAgentWithoutDslAttachment: SystemAgent? = null

        /* Пользователи */
        var testActiveUser: User? = null
        var testDeletedUser: User? = null
    }

    fun clearData() {
        testActiveUser = null
        testDeletedUser = null
        testDslFileContentRef1 = null
        testDslFileContentRef2 = null
        testAgentWithOneDslAttachment = null
        testAgentWithManyDslAttachment = null
        testAgentWithoutDslAttachment = null
    }

    /**
     * Создаём тестовые файлы
     */
    fun createData() {
        /* Пользователи */
        testActiveUser = createNotDeletedUser()
        testDeletedUser = createDeletedUser()
        val notDeletedUserId = testActiveUser!!.id!!

        /* Агенты */
        /* Агент с 1 прикреплением */
        val agentId = createAgent(notDeletedUserId, notDeletedUserId)
        testDslFileContentRef1 = createDslFileContentRef(agentId, testDskFileContentRef1Data)
        /* Агент с 2 и более прикреплениями */
        val attachmentsAgentId = createAgent(notDeletedUserId, notDeletedUserId)
        testDslFileContentRef2 = createDslFileContentRefWithEndDate(attachmentsAgentId, testDskFileContentRef1Data)
        testDslFileContentRef2 = createDslFileContentRef(attachmentsAgentId, testDskFileContentRef2Data)
        testAgentWithOneDslAttachment = agentService.getById(agentId)
        testAgentWithManyDslAttachment = agentService.getById(attachmentsAgentId)
        testAgentWithoutDslAttachment = agentService.getById(createAgent(notDeletedUserId, notDeletedUserId))
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
        val login = randomString()
        val password = randomString()
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

    private fun createAgent(ownerId: Long, createUserId: Long): Long {
        return agentService.save(SystemAgent(randomString(), randomString(), true, ownerId, createUserId))
    }

    private fun randomString(): String {
        return UUID.randomUUID().toString()
    }
}