package agentcore.agentfoundation

/**
 * @author Nikita Gorodilov
 */
abstract class AAgentCommand {

    @Throws(Exception::class)
    abstract fun start()
    abstract fun stop()
    protected abstract fun onStart()
    protected abstract fun onStop()
}
