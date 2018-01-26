package com.mycompany.dsl.base

import com.mycompany.db.base.Codable

/**
 * Системные сообщения агента для уведомления в dsl
 *
 * @author Nikita Gorodilov
 */
enum class SystemEvent(override val code: String): Codable<String> {
    /**
     * Первое действие агента при старте
     */
    AGENT_START("agentStart"),
    /**
     * Последние действия действия агента перед выключением
     */
    AGENT_STOP("agentStop");
}