package com.mycompany.dsl.base.behavior

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.dsl.RuntimeAgent

/**
 * Класс обновляет интерфейс в UI при каком либо событии агента
 *
 * @author Nikita Gorodilov
 */
class RuntimeAgentUpdateUiEventHistoryBehavior(
        historyService: SystemAgentEventHistoryService,
        private val appendHistoryText: (systemAgent: SystemAgent, message: String) -> Unit
) : RuntimeAgentHistoryEventBehavior(historyService) {

    override fun bing(runtimeAgent: RuntimeAgent) {
        super.bing(runtimeAgent)
        this.runtimeAgent = runtimeAgent
        onEvent("Добавление RuntimeAgentUpdateUiEventHistoryBehavior к агенту")
    }

    override fun unbind() {
        super.unbind()
        onEvent("Отсоединение RuntimeAgentUpdateUiEventHistoryBehavior от агента")
    }

    override fun onEvent(message: String) {
        appendHistoryText(runtimeAgent.getSystemAgent(), "$message\n")
    }
}