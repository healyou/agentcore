package com.mycompany.agent

import com.mycompany.AuthBasePage
import com.mycompany.BootstrapFeedbackPanel
import com.mycompany.agent.panels.DslFileUploadPanel
import com.mycompany.agent.validator.ServiceLoginValidator
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.security.acceptor.AlwaysAcceptedPrincipalAcceptor
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.RestartResponseAtInterceptPageException
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.CheckBox
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.lang.Bytes
import org.apache.wicket.util.string.StringValueConversionException
import java.util.*

/**
 * Страница просмотра/редактирование агента
 *
 * @author Nikita Gorodilov
 */
class AgentPage(parameters: PageParameters) : AuthBasePage(parameters) {

    companion object {
        @JvmStatic
        val AGENT_ID_PARAMETER_NAME = "id"
    }

    private enum class PageMode {
        CREATE, EDIT, VIEW
    }

    @SpringBean
    private lateinit var agentService: SystemAgentService

    private lateinit var feedback: BootstrapFeedbackPanel
    private lateinit var buttons: WebMarkupContainer
    private lateinit var form: Form<SystemAgent>
    private lateinit var agentInfoLabel: Label

    private var agent: SystemAgent
    private var mode: PageMode

    init {
        val idParameter = parameters.get(AGENT_ID_PARAMETER_NAME)

        if (!idParameter.isEmpty) {
            // EDIT
            try {
                val agentId = idParameter.toLongObject()
                agent = agentService.getById(agentId)
                mode = PageMode.VIEW
            } catch (e: StringValueConversionException) {
                throw RestartResponseAtInterceptPageException(application.homePage)
            }
        } else {
            // CREATE
            if (!isPrincipalHasAnyAuthority(Authority.CREATE_OWN_AGENT)) {
                throw RestartResponseAtInterceptPageException(application.homePage)
            }
            val currentUserId = getPrincipal().user.id!!
            agent = SystemAgent("", "", true, currentUserId, currentUserId)
            mode = PageMode.CREATE
        }
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return HasAnyAuthorityPrincipalAcceptor(Authority.VIEW_OWN_AGENT, Authority.EDIT_OWN_AGENT)
    }

    override fun onInitialize() {
        super.onInitialize()

        agentInfoLabel = Label("agentInfoLabel", object : AbstractReadOnlyModel<String>() {
            override fun getObject(): String {
                return getInfoLabelText()
            }
        })
        add(agentInfoLabel.setOutputMarkupId(true))
        feedback = BootstrapFeedbackPanel("feedback")
        add(feedback)

        val agentModel = CompoundPropertyModel(agent)
        form = Form<SystemAgent>("form", agentModel)
        add(form)
        form.isMultiPart = true
        form.fileMaxSize = Bytes.megabytes(1)
        form.maxSize = Bytes.megabytes(1.5)

        // agent summary panel
        form.add(object : TextField<String>("serviceLogin") {
            override fun onConfigure() {
                super.onConfigure()
                isEnabled = isEditMode() || isCreateMode()
            }
        }.add(ServiceLoginValidator(agentModel)).setRequired(true))
        form.add(object : TextField<String>("servicePassword") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isCreateMode()
            }
        }.setRequired(true))
        form.add(object : DslFileUploadPanel("dslFile") {
            override fun onConfigure() {
                super.onConfigure()
                isEnabled = isEditMode() || isCreateMode()
            }
        }.setRequired(true))
        form.add(TextField<Long>("ownerId").setRequired(true).setEnabled(false))
        form.add(TextField<Long>("createUserId").setRequired(true).setEnabled(false))
        form.add(TextField<Date>("createDate").setRequired(true).setEnabled(false))
        form.add(TextField<Date>("updateDate").setEnabled(false))
        form.add(object : CheckBox("isDeleted") {
            override fun onConfigure() {
                super.onConfigure()
                isEnabled = isEditMode() || isCreateMode()
            }
        }.setRequired(true))
        form.add(object : CheckBox("isSendAndGetMessages") {
            override fun onConfigure() {
                super.onConfigure()
                isEnabled = isEditMode() || isCreateMode()
            }
        }.setRequired(true))
        form.add(Label("isDeletedLabel", Model.of(getString("isDeleted"))))
        form.add(Label("isSendAngGetMessagesLabel", Model.of(getString("isSendAndGetMessages"))))

        buttons = WebMarkupContainer("buttons")
        form.add(buttons.setOutputMarkupId(true))
        buttons.add(object : AjaxSubmitLink("save") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isEditMode() || isCreateMode()
            }

            override fun onSubmit(target: AjaxRequestTarget, form: Form<*>) {
                super.onSubmit(target, form)
                saveButtonClick(target)
            }

            override fun onError(target: AjaxRequestTarget, form: Form<*>) {
                super.onError(target, form)
                target.add(feedback)
            }
        })
        buttons.add(object : AjaxLambdaLink<Any>("edit", this::editButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isViewMode() && !isCreateMode() && (isPrincipalHasAnyAuthority(Authority.EDIT_OWN_AGENT) && isOwnAgent(agent, agentService)
                        || isPrincipalHasAnyAuthority(Authority.EDIT_ALL_AGENTS))
            }
        })
        buttons.add(object : AjaxLambdaLink<Any>("cancel", this::cancelButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isEditMode()
            }
        })
    }

    private fun saveButtonClick(target: AjaxRequestTarget) {
        agent.id = agentService.save(agent)
        agent = agentService.getById(agent.id!!)
        form.model = CompoundPropertyModel<SystemAgent>(agent)

        mode = PageMode.VIEW
        updatePage(target)
    }

    private fun editButtonClick(target: AjaxRequestTarget) {
        mode = PageMode.EDIT
        updatePage(target)
    }

    private fun cancelButtonClick(target: AjaxRequestTarget) {
        mode = PageMode.VIEW
        updatePage(target)
    }

    private fun updatePage(target: AjaxRequestTarget) {
        target.add(buttons)
        target.add(form)
        target.add(agentInfoLabel)
    }

    private fun isEditMode(): Boolean {
        return mode == PageMode.EDIT
    }

    private fun isViewMode(): Boolean {
        return mode == PageMode.VIEW
    }

    private fun isCreateMode(): Boolean {
        return mode == PageMode.CREATE
    }

    private fun getInfoLabelText(): String {
        return if (isCreateMode()) "Создание агента" else "Агент № ${agent.id}"
    }
}