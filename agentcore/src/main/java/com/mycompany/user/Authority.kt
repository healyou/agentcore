package com.mycompany.user

import com.mycompany.db.base.Codable

/**
 * Привилегии пользователя
 *
 * @author Nikita Gorodilov
 */
enum class Authority(override val code: String): Codable<String> {

    /**
     * Создание агента
     */
    CREATE_AGENT("create_agent"),

    /**
     * Редактирование данных своего агента
     */
    EDIT_OWN_AGENT("edit_own_agent"),

    /**
     * Редактирование данных всех агентов
     */
    EDIT_ALL_AGENTS("edit_all_agent"),

    /**
     * Просмотр списка всех агентов
     */
    VIEW_ALL_AGENTS("view_all_agents"),

    /**
     * Просмотр списка своих агентов
     */
    VIEW_OWN_AGENT("view_own_agents");
}