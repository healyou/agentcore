package agentcore.database.dto;

import java.io.Serializable;

/**
 * @author Nikita Gorodilov
 */
public interface ABaseDtoEntity extends Serializable {

    // непонятно, нужна ли эта функция, ведь какой id объекта будет на сервере, какой вернулся
    // возможна путаница из-за Serialization
    //Long getId();
}
