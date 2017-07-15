package agentcore.agentcommunication

import agentcore.database.dto.MessageLocalDataDto

/**
 * Сообщения, содержащие информацию о решении агентов
 *
 * @author Nikita Gorodilov
 */
abstract class ASolutionMessage(val dtoEntity: MessageLocalDataDto) : Message {

}