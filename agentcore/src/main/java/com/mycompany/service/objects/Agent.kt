package com.mycompany.service.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Агент
 *
 * @author Nikita Gorodilov
 */
class Agent @JsonCreator constructor (
        @JsonProperty("id")
        override var id: Long?,
        @JsonProperty("masId")
        var masId: String,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("type")
        var type: AgentType,
        @JsonProperty("createDate")
        var createDate: Date,
        @JsonProperty("deleted")
        var isDeleted: Boolean
): Entity