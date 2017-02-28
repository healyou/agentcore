package agentcommunication.message;

import database.dto.DtoEntityImpl;

/**
 * Created on 28.02.2017 19:13
 * Коллективное решение задачи
 *
 * @autor Nikita Gorodilov
 */
public class MCollectiveSolution extends AMessage {

    private DtoEntityImpl dtoEntity;
    private Object solutionId;

    public MCollectiveSolution(DtoEntityImpl dtoEntity, Object solutionId) {
        this.dtoEntity = dtoEntity;
        this.solutionId = solutionId;
    }

    public DtoEntityImpl getDtoEntity() {
        return dtoEntity;
    }

    public Object getSolutionId() {
        return solutionId;
    }

}
