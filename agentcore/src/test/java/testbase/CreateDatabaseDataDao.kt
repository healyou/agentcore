package testbase

import db.base.AbstractDao
import db.core.file.dslfile.DslFileContentRef
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author Nikita Gorodilov
 */
@Component
class CreateDatabaseDataDao : AbstractDao() {

    @Autowired
    private lateinit var agentService: SystemAgentService

    companion object {
        val testDskFileContentRef1Data = byteArrayOf(1,2,3)
        var testDslFileContentRef1: DslFileContentRef? = null
        val testDskFileContentRef2Data = byteArrayOf(1,2,3,4,5)
        var testDslFileContentRef2: DslFileContentRef? = null

        var testAgentWithOneDslAttachment: SystemAgent? = null
        /* 1 рабочая, другие старые - не используются */
        var testAgentWithManyDslAttachment: SystemAgent? = null
    }

    /**
     * Создаём тестовые файлы
     */
    fun createData() {
        /* Агент с 1 прикреплением */
        val agentId = createAgent()
        testDslFileContentRef1 = createDslFileContentRef(agentId, testDskFileContentRef1Data)

        /* Агент с 2 и более прикреплениями */
        val attachmentsAgentId = createAgent()
        testDslFileContentRef2 = createDslFileContentRefWithEndDate(attachmentsAgentId, testDskFileContentRef1Data)
        testDslFileContentRef2 = createDslFileContentRef(attachmentsAgentId, testDskFileContentRef2Data)

        testAgentWithOneDslAttachment = agentService.get(agentId)
        testAgentWithManyDslAttachment = agentService.get(attachmentsAgentId)
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