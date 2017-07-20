package agentcore.agentcommunication

import java.io.IOException

/**
 * @author Nikita Gorodilov
 */
interface IAgentCommunication {

    @Throws(IOException::class)
    fun sendMassege(message: Message)

    @Throws(IOException::class)
    fun connect(host: String, port: Int)

    @Throws(IOException::class)
    fun disconnect()

    fun isConnect(): Boolean
}