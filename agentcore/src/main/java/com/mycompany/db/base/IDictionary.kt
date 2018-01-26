package com.mycompany.db.base

import com.mycompany.service.objects.Entity

/**
 * @author Nikita Gorodilov
 */
interface IDictionary<out T: Codable<out Any>>: Entity {

    val code: T
    val name: String
    val isDeleted: Boolean
}