package agentcore.agentcommunication;

import agentcore.database.dto.LocalDataDto;

/**
 * Created on 28.02.2017 19:12
 * Запрос на поиск коллективного решения и ответ на запрос
 *
 * @autor Nikita Gorodilov
 */
public class MSearchSolution extends AMessage {

    private LocalDataDto dataDto;

    public MSearchSolution(LocalDataDto dataDto) {
        this.dataDto = dataDto;
    }

    public LocalDataDto getDtoEntity() {
        return dataDto;
    }
}
