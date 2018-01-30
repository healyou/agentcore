package com.mycompany.agent

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.SystemAgentObjects
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
class AgentPageSpecification extends WebPageSpecification {

    private agentId = 1L
    private agentService = Mock(SystemAgentService.class)

    def setup() {
        signIn()
        putBean(agentService)
    }

    def "Страница для создания агента открывается успешно"() {
        when:
        agentService.getById(agentId) >>
                SystemAgentObjects.systemAgent(agentId, currentUser.user.id, currentUser.user.id)
        tester.startPage(new AgentPage(new PageParameters()))

        then:
        tester.assertRenderedPage(AgentPage.class)
    }

    def "Страница отображается в соответсвии с моделью"() {
        when:
        def agent = SystemAgentObjects.systemAgent(agentId, currentUser.user.id, currentUser.user.id)
        agentService.getById(agentId) >> agent
        tester.startPage(startEditAgentPage(agentId))

        then:
        tester.assertModelValue("form:serviceLogin", agent.serviceLogin)
        tester.assertRequired("form:serviceLogin")

        /* недоступен в режиме редактирования */
        tester.assertInvisible("form:servicePassword")

        tester.assertModelValue("form:dslFile", agent.dslFile)
        tester.assertRequired("form:dslFile")

        tester.assertModelValue("form:ownerId", agent.ownerId)
        tester.assertRequired("form:ownerId")

        tester.assertModelValue("form:createUserId", agent.createUserId)
        tester.assertRequired("form:createUserId")

        tester.assertModelValue("form:createDate", agent.createDate)
        tester.assertRequired("form:createDate")

        tester.assertModelValue("form:updateDate", agent.updateDate)
        tester.assertNotRequired("form:updateDate")

        tester.assertModelValue("form:isDeleted", agent.isDeleted)
        tester.assertRequired("form:isDeleted")

        tester.assertModelValue("form:isSendAndGetMessages", agent.isSendAndGetMessages)
        tester.assertRequired("form:isSendAndGetMessages")
    }

    private static AgentPage startEditAgentPage(Long agentId) {
        def parameters = new PageParameters()
        parameters.add(AgentPage.AGENT_ID_PARAMETER_NAME, agentId)
        new AgentPage(parameters)
    }
}
