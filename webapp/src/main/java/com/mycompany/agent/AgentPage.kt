package com.mycompany.agent

import com.mycompany.AuthBasePage
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.security.acceptor.AlwaysAcceptedPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.CheckBox
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.string.StringValueConversionException
import java.util.*

/**
 * Страница агента
 *
 * @author Nikita Gorodilov
 */
class AgentPage(parameters: PageParameters) : AuthBasePage(parameters) {

    companion object {
        val AGENT_ID_PARAMETER_NAME = "id"
    }

    @SpringBean
    private lateinit var agentService: SystemAgentService

    private val agent: SystemAgent

    init {
        val idParameter = parameters.get(AGENT_ID_PARAMETER_NAME)

        if (!idParameter.isEmpty) {
            try {
                val agentId = idParameter.toLongObject()
                agent = agentService.getById(agentId)
            } catch (e: StringValueConversionException) {
                throw RestartResponseAtInterceptPageException(application.homePage)
            }
        } else {
            throw RestartResponseAtInterceptPageException(application.homePage)
        }
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return AlwaysAcceptedPrincipalAcceptor()
    }

    override fun onInitialize() {
        super.onInitialize()

        add(Label("agentInfoLabel", Model.of("Агент № ${agent.id}")))

        // agent summary panel
        val form = Form<SystemAgent>("form", CompoundPropertyModel(agent))
        add(form.setEnabled(false))
        form.add(TextField<String>("serviceLogin"))
        form.add(TextField<String>("ownerId"))
        form.add(TextField<String>("createUserId"))
        form.add(TextField<Date>("createDate"))
        form.add(TextField<Date>("updateDate"))
        form.add(CheckBox("isDeleted"))
        form.add(CheckBox("isSendAndGetMessages"))
        form.add(Label("isDeletedLabel", Model.of(getString("isDeleted"))))
        form.add(Label("isSendAngGetMessagesLabel", Model.of(getString("isSendAndGetMessages"))))
        form.add(object : AjaxSubmitLink("save") {
            override fun onSubmit(target: AjaxRequestTarget, form: Form<*>) {
                super.onSubmit(target, form)
            }

            override fun onError(target: AjaxRequestTarget, form: Form<*>) {
                super.onError(target, form)
            }
        })
    }
}