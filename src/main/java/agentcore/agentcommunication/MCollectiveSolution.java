package agentcore.agentcommunication;

import agentcore.database.dto.LocalDataDto;

/**
 * Created on 28.02.2017 19:13
 * Коллективное решение задачи
 *
 * @autor Nikita Gorodilov
 */
public class MCollectiveSolution extends AMessage {

    private LocalDataDto dataDto;
    private Integer solutionId;

    public MCollectiveSolution(LocalDataDto dtoEntity, Integer solutionId) {
        this.dataDto = dtoEntity;
        this.solutionId = solutionId;
    }

    public LocalDataDto getDtoEntity() {
        return dataDto;
    }

    public Integer getSolutionId() {
        return solutionId;
    }

}
