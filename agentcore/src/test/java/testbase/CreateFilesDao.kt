package testbase

import db.base.AbstractDao
import db.core.file.DslFileContentRef
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author Nikita Gorodilov
 */
@Component
class CreateFilesDao: AbstractDao() {

    @Autowired
    private lateinit var agentService: SystemAgentService

    companion object {
        val testDskFileContentRef1Data = byteArrayOf(1,2,3)
        var testDslFileContentRef1: DslFileContentRef? = null
    }

    /**
     * Создаём тестовые файлы
     */
    fun createFiles() {
        val agentId = agentService.create(SystemAgent(randomString(), randomString(), true))
        val filename = randomString()

        jdbcTemplate.update(
                "INSERT INTO dsl_file (agent_id, filename, data) VALUES (?, ?, ?)",
                agentId,
                filename,
                testDskFileContentRef1Data
        )

        testDslFileContentRef1 = DslFileContentRef(getSequence("dsl_file"), filename)
    }

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }
}