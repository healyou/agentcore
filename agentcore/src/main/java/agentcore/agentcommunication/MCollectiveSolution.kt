package agentcore.agentcommunication

import agentcore.database.dto.MessageLocalDataDto

/**
 * Коллективное решение задачи

 * @author Nikita Gorodilov
 */
class MCollectiveSolution(dtoEntity: MessageLocalDataDto, val solutionId: Int?) : ASolutionMessage(dtoEntity)
