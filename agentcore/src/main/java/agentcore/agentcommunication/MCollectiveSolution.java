package agentcore.agentcommunication;

import agentcore.database.dto.MessageLocalDataDto;

/**
 * Коллективное решение задачи
 *
 * @author Nikita Gorodilov
 */
public class MCollectiveSolution extends ASolutionMessage {

    private Integer solutionId;

    public MCollectiveSolution(MessageLocalDataDto dtoEntity, Integer solutionId) {
        super(dtoEntity);
        this.solutionId = solutionId;
    }

    public Integer getSolutionId() {
        return solutionId;
    }
}
