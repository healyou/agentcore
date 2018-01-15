package com.mycompany.db.jdbc.systemagent

import com.mycompany.db.core.systemagent.SystemAgentEventHistory

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentEventHistoryDao {

    fun create(history: SystemAgentEventHistory): Long

    // TODO неверно грузит данные - сортировать надо наоборот
    fun getLastNumberItems(systemAgentId: Long, size: Long): List<SystemAgentEventHistory>
}