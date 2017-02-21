package agentfoundation.agentlifecicle.base;

/**
 * Created by user on 21.02.2017.
 */
public abstract class AAgentLifecicle {

    protected abstract void onInit();
    protected abstract void onStart();
    protected abstract void onPause();
    protected abstract void onResume();
    protected abstract void onStop();

}
