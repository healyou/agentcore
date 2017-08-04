package service.objects

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Объект агент в сервисе
 *
 * @author Nikita Gorodilov
 */
class Agent : Entity {

    override var id: Long? = null
    var masId: String? = null
    var name: String? = null
    var type: AgentType? = null
    var createDate: Date? = null
    @JsonProperty("deleted")
    var isDeleted: Boolean? = null
}