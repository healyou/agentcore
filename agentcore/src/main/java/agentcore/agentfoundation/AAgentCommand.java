package agentcore.agentfoundation;

/**
 * @author Nikita Gorodilov
 */
public abstract class AAgentCommand {

    public abstract void start() throws Exception;
    public abstract void stop();

    protected abstract void onStart();
    protected abstract void onStop();

}
