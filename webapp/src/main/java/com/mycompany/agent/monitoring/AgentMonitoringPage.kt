package com.mycompany.agent.monitoring

import com.mycompany.AuthBasePage
import com.mycompany.BootstrapFeedbackPanel
import com.mycompany.HomePage
import com.mycompany.agent.AgentPage
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.base.converter.BooleanYesNoConverter
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.security.acceptor.AlwaysAcceptedPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.convert.IConverter

/**
 * Страница мониторинга состояния агентов
 *
 * @author Nikita Gorodilov
 */
class AgentMonitoringPage : AuthBasePage() {

    companion object {
        private val TABLE_MAX_SHOW_SIZE = 10
    }

    @SpringBean
    private lateinit var agentService: SystemAgentService

    // TODO - общий класс для таблиц
    private var tableSizeNumber: Long = agentService.size()
    private var tableShowNumber: Integer =
            if (tableSizeNumber < TABLE_MAX_SHOW_SIZE) Integer(tableSizeNumber.toInt()) else Integer(TABLE_MAX_SHOW_SIZE)

    private lateinit var feedback: BootstrapFeedbackPanel
    private lateinit var buttons: WebMarkupContainer
    private lateinit var listViewContainer: WebMarkupContainer
    private lateinit var tableNumberLabel: Label

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        // <!-- Page level plugin CSS-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/datatables/dataTables.bootstrap4.css")))

        // <!-- Custom scripts for this page - index -->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin-datatables.min.js")))
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return AlwaysAcceptedPrincipalAcceptor()
    }

    override fun onInitialize() {
        super.onInitialize()

        feedback = BootstrapFeedbackPanel("feedback")
        val showNumberModel = PropertyModel.of<Integer>(this, "tableShowNumber")
        tableNumberLabel = Label("tableNumberLabel", showNumberModel)
        listViewContainer = WebMarkupContainer("listViewContainer")
        listViewContainer.add(object : ListView<SystemAgent>("listView", configureAgentListModel()) {
            override fun populateItem(item: ListItem<SystemAgent>) {
                val agent = item.modelObject

                item.add(object : AjaxCheckBox("check", Model.of(isCheck(agent))) {
                    public override fun onUpdate(target: AjaxRequestTarget) {
                        checkAgent(target, agent, modelObject)
                    }
                })
                item.add(object : AjaxLink<Any>("id") {
                    override fun onConfigure() {
                        super.onConfigure()
                        isEnabled = isPrincipalHasAnyAuthority(Authority.VIEW_OWN_AGENT, Authority.EDIT_OWN_AGENT)
                    }

                    override fun onInitialize() {
                        super.onInitialize()
                        add(Label("idLabel", PropertyModel.of<Long>(agent, "id")))
                    }

                    override fun onClick(target: AjaxRequestTarget) {
                        agentClick(agent)
                    }
                })
                item.add(Label("login", PropertyModel.of<String>(agent, "serviceLogin")))
                item.add(object : Label("isDeleted", PropertyModel.of<Boolean>(agent, "isDeleted")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return BooleanYesNoConverter() as IConverter<C>
                    }
                })
                item.add(Label("status", Model.of(configureAgentStatusLabel(agent))))
            }
        })

        add(listViewContainer.setOutputMarkupId(true))
        add(tableNumberLabel.setOutputMarkupId(true))
        add(feedback)
        add(Label("tableSizeLabel", PropertyModel.of<Long>(this, "tableSizeNumber")))

        buttons = object : WebMarkupContainer("buttons") {
            override fun onConfigure() {
                super.onConfigure()
                // TODO - если есть права на старт стоп агентов
                //isVisible = isEditMode()
            }
        }
        add(buttons)
        buttons.add(object : AjaxLambdaLink<Any>("start", this::startButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                // TODO - если всех агентов можно запустить
//                isVisible = isViewMode() && !isCreateMode() && (isPrincipalHasAnyAuthority(Authority.EDIT_OWN_AGENT) && isOwnAgent(agent, agentService)
//                        || isPrincipalHasAnyAuthority(Authority.EDIT_ALL_AGENTS))
            }
        })
        buttons.add(object : AjaxLambdaLink<Any>("stop", this::stopButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                // TODO - если всех агентов можно остановить в работе
                //isVisible = isEditMode()
            }
        })
    }

    private fun startButtonClick(target: AjaxRequestTarget) {
        // TODO
    }

    private fun stopButtonClick(target: AjaxRequestTarget) {
        // TODO
    }

    /**
     * Текст указывающий текущий статус агента
     */
    private fun configureAgentStatusLabel(agent: SystemAgent): String {
        return "Не запущен" // TODO выбор статуса из системы запуска агентов
    }

    private fun configureAgentListModel(): IModel<List<SystemAgent>> {
        return object : AbstractReadOnlyModel<List<SystemAgent>>() {
            override fun getObject(): List<SystemAgent> {
                return loadTableData()
            }
        }
    }

    private fun loadTableData(): List<SystemAgent> {
        return agentService.get(tableShowNumber.toLong())
    }

    private fun checkAgent(target: AjaxRequestTarget, agent: SystemAgent, check: Boolean) {
        // TODO таблицу надо перегрузить и кнопки(можно запускать или нельзя для проверки)
    }

    private fun agentClick(agent: SystemAgent) {
        val parameters = PageParameters()
        parameters.set(AgentPage.AGENT_ID_PARAMETER_NAME, agent.id!!)
        setResponsePage(AgentPage::class.java, parameters)
    }

    /**
     * Выбран ли агент на страницу
     */
    private fun isCheck(agent: SystemAgent): Boolean {
        // TODO
        return false
    }
}