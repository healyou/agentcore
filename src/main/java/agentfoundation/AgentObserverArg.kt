package agentfoundation

/**
 * Created on 27.03.2017 19:31
 * @autor Nikita Gorodilov
 */
data class AgentObserverArg(
        var arg: Any? = null,
        var type: ObserverArgType = ObserverArgType.DEFAUL_VALUE
)

enum class ObserverArgType {
    DEFAUL_VALUE,
    OUTPUT_DATA,
    MESSAGE
}