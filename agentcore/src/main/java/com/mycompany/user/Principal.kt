package com.mycompany.user

import java.util.*

/**
 * Авторизованный пользователь
 *
 * @author Nikita Gorodilov
 */
class Principal(
        /* Пользователь */
        var user: User,
        /* Список прав */
        var authorities: Set<Authority> = EnumSet.noneOf(Authority::class.java)
)