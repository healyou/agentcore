package agentcore.database.dto

import agentcore.utils.Codable

/**
 * Типы данных
 *
 * @author Nikita Gorodilov
 */
enum class InputDataType constructor(override val code: String) : Codable<String> {
    STRING("String"),
    INT("int"),
    DOUBLE("double");
}
