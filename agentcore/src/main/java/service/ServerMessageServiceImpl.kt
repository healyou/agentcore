package service

import agentcore.database.base.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import service.objects.Message
import service.objects.SendMessageData

/**
 * @author Nikita Gorodilov
 */
@Component
open class ServerMessageServiceImpl(@Autowired final override val environment: Environment) : AbstractAgentService(), ServerMessageService {

    override val BASE_URL: String = environment.getProperty("agent.service.base.url")

    override fun sendMessage(sessionManager: SessionManager, data: SendMessageData): Message? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessages(sessionManager: SessionManager): List<Message>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}