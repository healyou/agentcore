package agentcore.agentfoundation

import java.util.Observable

/**
 * Created by user on 21.02.2017.
 */
abstract class IAgentBrain : Observable() {

    abstract fun takeInputData()
    abstract fun calculateOutput()
}
