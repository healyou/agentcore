package db.base

import agentcore.utils.Codable
import service.objects.Entity

/**
 * @author Nikita Gorodilov
 */
interface IDictionary<out T: Codable<out Any>>: Entity {

    val code: T
    val name: String
    val isDeleted: Boolean
}