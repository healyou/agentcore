package service.objects

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Nikita Gorodilov
 */
class AgentType : Entity {

    override var id: Long? = null
    var code: String? = null
    var name: String? = null
    @JsonProperty("deleted")
    var isDeleted: Boolean? = null
}