package dsl.base.behavior

import db.core.systemagent.SystemAgentEventHistoryService

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentLogEventBehavior(historyService: SystemAgentEventHistoryService)
    : RuntimeAgentHistoryEventBehavior(historyService) {

    // TODO логирование действий
}