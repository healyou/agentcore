package agentcore.agentfoundation

import java.util.Observable

/**
 * @author Nikita Gorodilov
 */
abstract class IAgentBrain : Observable() {

    abstract fun takeInputData()
    abstract fun calculateOutput()
}
