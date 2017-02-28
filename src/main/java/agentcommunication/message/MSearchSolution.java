package agentcommunication.message;

import database.dto.DtoEntityImpl;

/**
 * Created on 28.02.2017 19:12
 * Запрос на поиск коллективного решения и ответ на запрос
 *
 * @autor Nikita Gorodilov
 */
public class MSearchSolution extends AMessage {

    private DtoEntityImpl dtoEntity;

    public MSearchSolution(DtoEntityImpl dtoEntity) {
        this.dtoEntity = dtoEntity;
    }

    public DtoEntityImpl getDtoEntity() {
        return dtoEntity;
    }

}
