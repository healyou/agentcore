package agentcore.agentcommunication

import agentcore.database.dto.MessageLocalDataDto

/**
 * Запрос на поиск коллективного решения и ответ на запрос

 * @author Nikita Gorodilov
 */
class MSearchSolution(dtoEntity: MessageLocalDataDto) : ASolutionMessage(dtoEntity)
