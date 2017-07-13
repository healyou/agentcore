package agentcore.agentfoundation;

import java.util.Observable;

/**
 * Created by user on 21.02.2017.
 */
public abstract class IAgentBrain extends Observable {

    public abstract void takeInputData();
    public abstract void calculateOutput();

}
