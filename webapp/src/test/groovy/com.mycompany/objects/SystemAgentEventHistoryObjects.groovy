package com.mycompany.objects

import com.mycompany.db.core.systemagent.SystemAgentEventHistory

/**
 * @author Nikita Gorodilov
 */
class SystemAgentEventHistoryObjects {

    static final eventHistorys() {
        Arrays.asList(eventHistory(), eventHistory(), eventHistory())
    }

    static final def eventHistory() {
        def eventHistory = new SystemAgentEventHistory(1L, StringObjects.randomString)
        eventHistory.id = 1L
        eventHistory.createDate = new Date()
        eventHistory
    }
}
