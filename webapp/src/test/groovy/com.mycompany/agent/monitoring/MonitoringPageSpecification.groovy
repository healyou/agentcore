package com.mycompany.agent.monitoring

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl

/**
 * @author Nikita Gorodilov
 */
class MonitoringPageSpecification extends WebPageSpecification {

    private agentService = Mock(SystemAgentService.class) {
        get(_,_) >> []
    }

    def setup() {
        signIn()
        putBean(agentService)
        putBean(Mock(ServiceMessageService.class))
        putBean(Mock(SystemAgentEventHistoryService.class))
        putBean(Mock(IRuntimeAgentWorkControl.class))
    }

    def "Страница успешно открывается"() {
        when:
        tester.startPage(new AgentsMonitoringPage())

        then:
        tester.assertRenderedPage(AgentsMonitoringPage.class)
    }
}
