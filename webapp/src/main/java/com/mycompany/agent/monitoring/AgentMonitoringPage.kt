package com.mycompany.agent.monitoring

import com.mycompany.AuthBasePage
import com.mycompany.agent.AgentPage
import com.mycompany.agent.panels.AgentMonitoringPanel
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.string.StringValueConversionException

/**
 * Страница мониторинга состояния агента
 *
 * @author Nikita Gorodilov
 */
class AgentMonitoringPage(parameters: PageParameters) : AuthBasePage(parameters) {

    companion object {
        val AGENT_MONITORING_PAGE_AGENT_ID_PARAMETER_NAME = "id"
    }

    @SpringBean
    private lateinit var agentService: SystemAgentService
    @SpringBean
    private lateinit var agentWorkControl: IRuntimeAgentWorkControl

    private val agent: SystemAgent

    init {
        val idParameter = parameters.get(AgentPage.AGENT_PAGE_AGENT_ID_PARAMETER_NAME)

        if (!idParameter.isEmpty) {
            try {
                val agentId = idParameter.toLongObject()
                agent = agentService.getById(agentId)
                if (!isOwnAgent(agent, agentService)) {
                    throw RestartResponseAtInterceptPageException(application.homePage)
                }
            } catch (e: StringValueConversionException) {
                throw RestartResponseAtInterceptPageException(application.homePage)
            }
        } else {
            throw RestartResponseAtInterceptPageException(application.homePage)
        }
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return HasAnyAuthorityPrincipalAcceptor(Authority.VIEW_OWN_AGENT)
    }

    override fun onInitialize() {
        super.onInitialize()

        add(Label("agentName", Model.of("${agent.serviceLogin} id-${agent.id!!}")))
        add(AgentMonitoringPanel("agentMonitoringPanel", Model.of(agent)))

        val agentWorkButtons = object : WebMarkupContainer("agentWorkButtons") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isPrincipalHasAnyAuthority(Authority.STARTED_OWN_AGENT)
            }
        }
        add(agentWorkButtons)
        agentWorkButtons.add(object : AjaxLambdaLink<Any>("start", this::startButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = !isStartedAgent()
            }
        })
        agentWorkButtons.add(object : AjaxLambdaLink<Any>("stop", this::stopButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isStartedAgent()
            }
        })
    }

    private fun isStartedAgent(): Boolean {
        return agentWorkControl.isStarted(agent)
    }

    private fun startButtonClick(target: AjaxRequestTarget) {
        agentWorkControl.start(agent)
    }

    private fun stopButtonClick(target: AjaxRequestTarget) {
        agentWorkControl.stop(agent)
    }
}