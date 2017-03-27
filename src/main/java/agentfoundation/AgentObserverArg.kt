package agentfoundation

/**
 * Created on 27.03.2017 19:31
 * @autor Nikita Gorodilov
 */
data class AgentObserverArg(
        open var arg: Any? = null,
        open var type: ObserverArgType = ObserverArgType.DEFAUL
)

enum class ObserverArgType {
    DEFAUL,
    OUTPUT_DATA,
    MESSAGE
}