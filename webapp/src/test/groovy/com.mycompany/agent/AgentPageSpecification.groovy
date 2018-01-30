package com.mycompany.agent

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.AgentObjects
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
class AgentPageSpecification extends WebPageSpecification {

    private agentId = 1L
    private agentService

    def setup() {
        signIn()
        putBean(agentService = Mock(SystemAgentService.class))
    }

    def "Страница для создания агента открывается успешно"() {
        when:
        agentService.getById(agentId) >> AgentObjects.agent(agentId)
        tester.startPage(new AgentPage(new PageParameters()))

        then:
        tester.assertRenderedPage(AgentPage.class)
    }
}
