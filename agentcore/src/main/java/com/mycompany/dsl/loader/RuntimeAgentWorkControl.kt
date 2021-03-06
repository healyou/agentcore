package com.mycompany.dsl.loader

import com.mycompany.agentworklibrary.ILibraryAgentWorkControl
import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.base.IRuntimeAgent
import com.mycompany.dsl.base.behavior.RuntimeAgentHistoryEventBehavior
import com.mycompany.dsl.exceptions.RuntimeAgentException
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import com.mycompany.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Nikita Gorodilov
 */
// TODO тесты работы данного класса
@Component
class RuntimeAgentWorkControl: IRuntimeAgentWorkControl, ILibraryAgentWorkControl {

    companion object {

        /**
         * Получение реального экземпляра объекта
         * Используется в библиоетеке функций агента
         */
        @JvmStatic
        fun getInstanceForAgentLibrary(): ILibraryAgentWorkControl {
            return InstantiationTracingBeanPostProcessor.runtimeAgentLoader as ILibraryAgentWorkControl
        }
    }

    @Autowired
    private lateinit var systemAgentService: SystemAgentService
    @Autowired
    private lateinit var serviceMessageService: ServiceMessageService
    @Autowired
    private lateinit var messageTypeService: ServiceMessageTypeService
    @Autowired
    private lateinit var serverTypeService: ServerTypeService
    @Autowired
    private lateinit var environment: Environment
    @Autowired
    private lateinit var loginService: LoginService
    @Autowired
    private lateinit var historyService: SystemAgentEventHistoryService
    @Autowired
    private lateinit var fileContentLocator: FileContentLocator

    /**
     * Используется ThreadPoolRuntimeAgent - чтобы вызов onLoadImage лишь давал сигнал начала выполнения функции агентом
     * Поэтому в данном потоке время выполнения функции будет минимальным
     */
    private val startedAgents = ConcurrentHashMap<Long, IRuntimeAgent>()

    override fun start() {
        // todo - пока и ненадо
    }

    override fun stop() {
        startedAgents.values.forEach {
            if (it.isStarted()) {
                it.stop()
            }
        }
        shutdownExecutors()
    }

    /**
     * одновременно может начать работу агентом лишь из 1-го потока
     * поэтому нельзя будет добавить одновременно двух одинаковых агентов
     */
    @Throws(RuntimeAgentException::class)
    override fun start(agent: SystemAgent) {
        createExecutors()
        synchronized(this) {
            if (agent.isNew) {
                throw RuntimeAgentException("Агент ещё не создан")
            } else if (isStarted(agent)) {
                throw RuntimeAgentException("Агент уже работает")
            }

            val runtimeAgent = object : ThreadPoolRuntimeAgent(agent.serviceLogin) {

                override fun getSystemAgentService(): SystemAgentService = this@RuntimeAgentWorkControl.systemAgentService
                override fun getServiceMessageService(): ServiceMessageService = this@RuntimeAgentWorkControl.serviceMessageService
                override fun getServerTypeService(): ServerTypeService = this@RuntimeAgentWorkControl.serverTypeService
                override fun getLoginService(): LoginService = this@RuntimeAgentWorkControl.loginService
                override fun getEnvironment(): Environment = this@RuntimeAgentWorkControl.environment
                override fun getMessageTypeService(): ServiceMessageTypeService = this@RuntimeAgentWorkControl.messageTypeService
                override fun getFileContentLocator(): FileContentLocator = this@RuntimeAgentWorkControl.fileContentLocator
                override fun getOwner(): User = agent.owner
                override fun getCreateUser(): User = agent.createUser
            }
            runtimeAgent.add(RuntimeAgentHistoryEventBehavior(historyService))
            runtimeAgent.start()
            startedAgents.put(agent.id!!, runtimeAgent)
        }
    }

    @Throws(RuntimeAgentException::class)
    override fun stop(agent: SystemAgent) {
        if (agent.isNew) {
            throw RuntimeAgentException("Агент ещё не создан")
        } else if(!isStarted(agent)) {
            throw RuntimeAgentException("Агент ещё не работает")
        }

        val agentId = agent.id!!
        val stoppedAgent = startedAgents.getValue(agentId)
        /* операции над агентами не могут быть выполнены одновременно n потоками */
        synchronized(stoppedAgent) {
            if (stoppedAgent.isStarted()) {
                stoppedAgent.stop()
            }
        }
        startedAgents.remove(agentId)
    }

    override fun isStarted(agent: SystemAgent): Boolean {
        if (agent.isNew) {
            throw RuntimeAgentException("Агент ещё не создан")
        }

        val agentId = agent.id!!
        return startedAgents.containsKey(agentId)
    }

    override fun isStart(agent: SystemAgent): Boolean {
        if (agent.isNew || agent.dslFile == null || isStarted(agent)) {
            return false;
        }
        return true
    }

    /**
     * Вызывается из библиотеки workLibrary
     */
    override fun onAgentEvent(agentId: Long, event: String) {
        val operationAgent: IRuntimeAgent
        try {
            operationAgent  = startedAgents.getValue(agentId)
        } catch (ignored: Exception) {
            return
        }

        /* операции над агентами не могут быть выполнены одновременно n потоками */
        synchronized(operationAgent) {
            operationAgent.onGetLocalMessage(DslLocalMessage(event))
        }
    }

    override fun getStartedAgents(): List<SystemAgent> {
        return startedAgents.values.map { it.getSystemAgent() }
    }

    // todo executor сюда вынести наверное
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