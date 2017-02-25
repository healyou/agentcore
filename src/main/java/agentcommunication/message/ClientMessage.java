package agentcommunication.message;

import agentcommunication.message.base.IMessage;
import database.dto.DtoEntityImpl;

import java.io.Serializable;

/**
 * Created by lappi on 22.02.2017.
 */
public class ClientMessage implements IMessage, Serializable {

    public enum ClientMessageType {
        // агент ищет решение задачи у коллектива
        SEARCH_SOLUTION,

        // агент выдаёт решение общей задачи
        GET_SOLUTION
    }

    private DtoEntityImpl dtoEntity;
    private ClientMessageType type;

    public ClientMessage(DtoEntityImpl dtoEntity, ClientMessageType type) {
        this.dtoEntity = dtoEntity;
        this.type = type;
    }

    public DtoEntityImpl getDtoEntity() {
        return dtoEntity;
    }

    public ClientMessageType getMessageType() {
        return type;
    }

}
