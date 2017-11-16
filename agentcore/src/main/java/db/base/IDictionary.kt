package db.base

import db.base.Codable
import service.objects.Entity

/**
 * @author Nikita Gorodilov
 */
interface IDictionary<out T: Codable<out Any>>: Entity {

    val code: T
    val name: String
    val isDeleted: Boolean
}