package agentcommunication.message;

import database.dto.DtoEntityImpl;

import java.io.Serializable;

/**
 * Created by lappi on 22.02.2017.
 */
public class ServerMessage implements Serializable {

    private DtoEntityImpl dtoEntity;

    public ServerMessage(DtoEntityImpl dtoEntity) {
        this.dtoEntity = dtoEntity;
    }

    public DtoEntityImpl getDtoEntity() {
        return dtoEntity;
    }

}
