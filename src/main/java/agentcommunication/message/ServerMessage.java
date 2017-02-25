package agentcommunication.message;

import agentcommunication.message.base.IMessage;
import database.dto.DtoEntityImpl;

import java.io.Serializable;

/**
 * Created by lappi on 22.02.2017.
 */
public class ServerMessage implements IMessage, Serializable {

    public enum ServerMessageType {
        // каждый агент ищет решение задачи
        SEARCH_COLLECTIVE_SOLUTION,

        // выдача агенту коллективного решения задачи
        GET_COLLECTIVE_SOLUTION
    }

    private DtoEntityImpl dtoEntity;
    private ServerMessageType type;

    public ServerMessage(DtoEntityImpl dtoEntity, ServerMessageType type) {
        this.dtoEntity = dtoEntity;
        this.type = type;
    }

    public DtoEntityImpl getDtoEntity() {
        return dtoEntity;
    }

    public ServerMessageType getMessageType() {
        return type;
    }

}
