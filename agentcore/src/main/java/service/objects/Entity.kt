package service.objects

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * @author Nikita Gorodilov
 */
interface Entity {

    var id: Long?

    val isNew
        @JsonIgnore
        get() = id == null
}