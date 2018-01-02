package com.mycompany.service.objects

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

/**
 * @author Nikita Gorodilov
 */
interface Entity: Serializable {

    var id: Long?

    val isNew
        @JsonIgnore
        get() = id == null
}