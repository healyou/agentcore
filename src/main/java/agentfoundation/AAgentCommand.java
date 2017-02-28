package agentfoundation;

/**
 * Created by user on 21.02.2017.
 */
public abstract class AAgentCommand {

    public abstract void start();
    public abstract void stop();

    protected abstract void onInit() throws Exception;
    protected abstract void onStart();
    protected abstract void onStop();

}
