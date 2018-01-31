package com.mycompany.agent.panels

import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.IModel
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.time.Duration

/**
 * Панель с автоматическим обновлением данных о сообщениях и истории действий агента агента
 *
 * @author Nikita Gorodilov
 */
// todo - чем отличается от AgentMonitoringPage - из названия непонятно
class AgentMonitoringPanel(id: String, private val model: IModel<SystemAgent>): Panel(id) {

    companion object {
        private val SHOW_LAST_SYSTEM_MESSAGES_NUMBER = 10
        private val SHOW_LAST_EVENT_HISTORY_NUMBER = 10
    }

    @SpringBean
    private lateinit var messageService: ServiceMessageService
    @SpringBean
    private lateinit var eventHistoryService: SystemAgentEventHistoryService

    private lateinit var messagesPanel: ServiceMessagesPanel
    private lateinit var eventHistoryPanel: AgentEventHistoryPanel

    private val serviceMessages = arrayListOf<ServiceMessage>()
    private val eventHistory = arrayListOf<SystemAgentEventHistory>()

    override fun onInitialize() {
        super.onInitialize()

        messagesPanel = ServiceMessagesPanel("serviceMessagesPanel", configureServiceMessagesModel())
        eventHistoryPanel = AgentEventHistoryPanel("eventHistoryPanel", configureEventHistoryModel())
        add(messagesPanel.setOutputMarkupId(true))
        add(eventHistoryPanel.setOutputMarkupId(true))

        add(object : AbstractAjaxTimerBehavior(Duration.milliseconds(2000)) {

            override fun onTimer(target: AjaxRequestTarget) {
                updatePanel(target)
            }
        })
    }

    private fun updatePanel(target: AjaxRequestTarget) {
        updateServiceMessagesList()
        updateEventHistoryList()
        target.add(messagesPanel, eventHistoryPanel)
    }

    private fun updateServiceMessagesList() {
        serviceMessages.clear()
        serviceMessages.addAll(messageService.getLastNumberItems(model.`object`.id!!,
                SHOW_LAST_SYSTEM_MESSAGES_NUMBER.toLong()))
    }

    private fun updateEventHistoryList() {
        eventHistory.clear()
        eventHistory.addAll(eventHistoryService.getLastNumberItems(model.`object`.id!!,
                SHOW_LAST_EVENT_HISTORY_NUMBER.toLong()))
    }

    private fun configureServiceMessagesModel(): IModel<List<ServiceMessage>> {
        return object : AbstractReadOnlyModel<List<ServiceMessage>>() {
            override fun getObject(): List<ServiceMessage> {
                return serviceMessages
            }
        }
    }

    private fun configureEventHistoryModel(): IModel<List<SystemAgentEventHistory>> {
        return object : AbstractReadOnlyModel<List<SystemAgentEventHistory>>() {
            override fun getObject(): List<SystemAgentEventHistory> {
                return eventHistory
            }
        }
    }
}