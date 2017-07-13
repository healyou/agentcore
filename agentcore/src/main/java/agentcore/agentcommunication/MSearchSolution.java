package agentcore.agentcommunication;

import agentcore.database.dto.LocalDataDto;

/**
 * Запрос на поиск коллективного решения и ответ на запрос
 *
 * @author Nikita Gorodilov
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
