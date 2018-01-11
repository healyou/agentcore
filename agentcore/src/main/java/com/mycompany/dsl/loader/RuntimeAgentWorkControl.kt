package com.mycompany.dsl.loader

import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.base.behavior.RuntimeAgentHistoryEventBehavior
import com.mycompany.dsl.exceptions.RuntimeAgentException
import com.mycompany.dsl.objects.DslImage
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import com.mycompany.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Nikita Gorodilov
 */
// TODO правка работы с многопоточностью
// TODO тесты работы данного класса
@Component
class RuntimeAgentWorkControl: IRuntimeAgentWorkControl {

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

    // TODO - надо, чтобы одновременно не могли делать операции с одним агентом
    private val startedAgents = ConcurrentHashMap<Long, RuntimeAgent>()

    override fun start() {
        TODO("not implemented")
    }

    override fun stop() {
        TODO("not implemented")
    }

    @Throws(RuntimeAgentException::class)
    override fun start(agent: SystemAgent) {
        if (agent.isNew) {
            throw RuntimeAgentException("Агент ещё не создан")
        } else if(isStarted(agent)) {
            throw RuntimeAgentException("Агент уже работает")
        }

        val agentId = agent.id!!
        val owner = User("", "")
        val createUser = User("", "")
        owner.id = agent.ownerId
        createUser.id = agent.createUserId
        val runtimeAgent = object : ThreadPoolRuntimeAgent(agent.serviceLogin) {

            override fun getSystemAgentService(): SystemAgentService = this@RuntimeAgentWorkControl.systemAgentService
            override fun getServiceMessageService(): ServiceMessageService = this@RuntimeAgentWorkControl.serviceMessageService
            override fun getServerTypeService(): ServerTypeService = this@RuntimeAgentWorkControl.serverTypeService
            override fun getLoginService(): LoginService = this@RuntimeAgentWorkControl.loginService
            override fun getEnvironment(): Environment = this@RuntimeAgentWorkControl.environment
            override fun getMessageTypeService(): ServiceMessageTypeService = this@RuntimeAgentWorkControl.messageTypeService
            override fun getFileContentLocator(): FileContentLocator = this@RuntimeAgentWorkControl.fileContentLocator
            override fun getOwner(): User = owner
            override fun getCreateUser(): User = createUser
        }
        runtimeAgent.add(RuntimeAgentHistoryEventBehavior(historyService))
        runtimeAgent.start()
        startedAgents.put(agentId, runtimeAgent)
    }

    // TODO - у агента нельзя одновременно вызывать start - stop - onLoadImage -> надо это всё как-то синхронизировать

    @Throws(RuntimeAgentException::class)
    override fun stop(agent: SystemAgent) {
        if (agent.isNew) {
            throw RuntimeAgentException("Агент ещё не создан")
        } else if(!isStarted(agent)) {
            throw RuntimeAgentException("Агент ещё не работает")
        }

        val agentId = agent.id!!
        startedAgents.getValue(agentId).apply {
            stop()
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

    override fun onLoadImage(agent: SystemAgent, image: DslImage) {
        if (agent.isNew) {
            throw RuntimeAgentException("Агент ещё не создан")
        } else if(!isStarted(agent)) {
            throw RuntimeAgentException("Агент ещё не работает")
        }

        val agentId = agent.id!!
        startedAgents.getValue(agentId).apply {
            onLoadImage(image)
        }
    }
}