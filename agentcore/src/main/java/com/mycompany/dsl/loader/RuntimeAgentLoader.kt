package com.mycompany.dsl.loader

import com.mycompany.db.core.file.ByteArrayFileContent
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.FileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.objects.DslImage
import java.io.File
import java.nio.file.Files

/**
 * Класс, выполняющий загрузку, запуск и остановку выполнения работы агентов
 *
 * @author Nikita Gorodilov
 */
class RuntimeAgentLoader: IRuntimeAgentLoader {

    companion object {
        private val AGENT_DSL_PATH = "data/dsl/"
        private val GROOVY_FILE_REGEX = "^.*.groovy$"
    }

    private val agents = arrayListOf<ThreadPoolRuntimeAgent>()

    override fun load(createAgent: (dslFile: DslFileAttachment) -> ThreadPoolRuntimeAgent) {
        File(AGENT_DSL_PATH).walk().forEach {
            if (GROOVY_FILE_REGEX.toRegex().matches(it.name)) {
                agents.add(createAgent(createDslFile(it.path, it.name)))
            }
        }
    }

    override fun start() {
        createExecutors()
        agents.forEach {
            it.start()
        }
    }

    override fun stop() {
        agents.forEach {
            it.stop()
        }
        shutdownExecutors()
        agents.clear()
    }

    override fun onLoadImage(agent: SystemAgent, image: DslImage) {
        val filterAgents = agents.filter {
            it.getSystemAgent().id == agent.id
        }
        if (filterAgents.isNotEmpty() && filterAgents.size == 1) {
            filterAgents[0].onLoadImage(image)
        }
    }

    private fun createDslFile(path: String, filename: String): DslFileAttachment {
        val file = File(path)
        val content = Files.readAllBytes(file.toPath())
        return DslFileAttachment(
                filename,
                object : FileContentRef {
                    @Override
                    override fun getContent(visitor: FileContentLocator): FileContent {
                        return ByteArrayFileContent(content)
                    }
                    @Override
                    override fun getName(): String {
                        return filename
                    }
                },
                content.size.toLong()
        )
    }

    private fun createExecutors() {
        val executor = ThreadPoolRuntimeAgent.executorService
        if (executor.isShutdown) {
            ThreadPoolRuntimeAgent.executorService = ThreadPoolRuntimeAgent.createExecutors()
        }
    }

    private fun shutdownExecutors() {
        val executor = ThreadPoolRuntimeAgent.executorService
        if (!executor.isShutdown) {
            val runnables = executor.shutdownNow()
        }
    }
}