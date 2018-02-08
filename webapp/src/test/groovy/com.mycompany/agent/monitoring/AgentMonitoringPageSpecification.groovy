package com.mycompany.agent.monitoring

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import com.mycompany.objects.SystemAgentObjects
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
class AgentMonitoringPageSpecification extends WebPageSpecification {

    private agentService = Mock(SystemAgentService.class) {
        isOwnAgent(_,_) >> true
    }
    private serviceMessageService = Mock(ServiceMessageService.class) {
        getLastNumberItems(_,_) >> []
    }
    private systemAgentEventHistoryService = Mock(SystemAgentEventHistoryService.class) {
        getLastNumberItems(_,_) >> []
    }

    def setup() {
        setupBeans()
        signIn()
    }

    def "Страница успешно открывается"() {
        when:
        def agent = createTestAgent()
        agentService.getById(_) >> agent
        tester.startPage(startAgentMonitoringPage(agent.id))

        then:
        tester.assertRenderedPage(AgentMonitoringPage.class)
    }

    private static AgentMonitoringPage startAgentMonitoringPage(Long agentId) {
        def parameters = new PageParameters()
        parameters.add(AgentMonitoringPage.AGENT_MONITORING_PAGE_AGENT_ID_PARAMETER_NAME, agentId)
        new AgentMonitoringPage(parameters)
    }

    private SystemAgent createTestAgent() {
        SystemAgentObjects.systemAgent(1L, currentUser.user, currentUser.user)
    }

    private def setupBeans() {
        putBean(agentService)
        putBean(serviceMessageService)
        putBean(systemAgentEventHistoryService)
        putBean(Mock(IRuntimeAgentWorkControl.class))
    }
}

