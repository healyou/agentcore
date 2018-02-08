package com.mycompany.agent

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.SystemAgentObjects
import com.mycompany.service.ServerAgentService
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
class AgentPageSpecification extends WebPageSpecification {

    private agentId = 1L
    private systemAgentService = Mock(SystemAgentService.class) {
        isExistsAgent(_) >> false
    }
    private serviceAgentService = Mock(ServerAgentService) {
        isExistsAgent(_,_) >> false
    }

    def setup() {
        signIn()
        putBean(systemAgentService)
        putBean(serviceAgentService)
    }

    def "Страница для создания агента открывается успешно"() {
        when:
        startAgentPageWithCreateMode()

        then:
        tester.assertRenderedPage(AgentPage.class)
    }

    def "Страница отображается в соответсвии с моделью"() {
        when:
        def agent = SystemAgentObjects.systemAgent(agentId, currentUser.user, currentUser.user)
        startAgentPageWithViewMode(agent)

        then:
        tester.assertModelValue("form:serviceLogin", agent.serviceLogin)
        tester.assertRequired("form:serviceLogin")

        /* недоступен в режиме редактирования */
        tester.assertInvisible("form:servicePassword")

        tester.assertModelValue("form:dslFile", agent.dslFile)
        tester.assertRequired("form:dslFile")

        tester.assertModelValue("form:owner.id", agent.owner.id)
        tester.assertRequired("form:owner.id")

        tester.assertModelValue("form:createUser.id", agent.createUser.id)
        tester.assertRequired("form:createUser.id")

        tester.assertModelValue("form:createDate", agent.createDate)
        tester.assertRequired("form:createDate")

        tester.assertModelValue("form:updateDate", agent.updateDate)
        tester.assertNotRequired("form:updateDate")

        tester.assertModelValue("form:isDeleted", agent.isDeleted)
        tester.assertRequired("form:isDeleted")

        tester.assertModelValue("form:isSendAndGetMessages", agent.isSendAndGetMessages)
        tester.assertRequired("form:isSendAndGetMessages")
    }

    def "Сохранение данных формы проходит успешно"() {
        setup:
        startAgentPageWithViewMode()

        when:
        agentPageEditButtonClick()
        agentPageSaveButtonClick()

        then:
        saveHasNoError()
    }

    private def startAgentPageWithCreateMode() {
        tester.startPage(new AgentPage(new PageParameters()))
    }

    private def startAgentPageWithViewMode() {
        startAgentPageWithViewMode(SystemAgentObjects.systemAgent(agentId, currentUser.user, currentUser.user))
    }

    private def startAgentPageWithViewMode(SystemAgent agent) {
        systemAgentService.getById(_) >> agent
        tester.startPage(startEditAgentPage(agent.id))
    }

    private def saveHasNoError() {
        !tester.getComponentFromLastRenderedPage("form").hasError()
    }

    private def agentPageSaveButtonClick() {
        def formTester = tester.newFormTester("form")
        formTester.submit("buttons:save")
    }

    private def agentPageEditButtonClick() {
        tester.executeAjaxEvent("form:buttons:edit", "click");
    }

    private static AgentPage startEditAgentPage(Long agentId) {
        def parameters = new PageParameters()
        parameters.add(AgentPage.AGENT_PAGE_AGENT_ID_PARAMETER_NAME, agentId)
        new AgentPage(parameters)
    }
}
