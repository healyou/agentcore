package agentcore.agentcommunication;

import agentcore.database.dto.MessageLocalDataDto;

/**
 * Запрос на поиск коллективного решения и ответ на запрос
 *
 * @author Nikita Gorodilov
 */
public class MSearchSolution extends ASolutionMessage {

    public MSearchSolution(MessageLocalDataDto dtoEntity) {
        super(dtoEntity);
    }
}
