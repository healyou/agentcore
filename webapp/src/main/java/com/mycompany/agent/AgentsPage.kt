package com.mycompany.agent

import com.mycompany.*
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.base.converter.BooleanYesNoConverter
import com.mycompany.base.converter.SqliteDateConverter
import com.mycompany.db.base.toSqlite
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.convert.IConverter
import org.apache.wicket.validation.validator.RangeValidator
import java.util.*

/**
 * Реестр агентов
 *
 * @author Nikita Gorodilov
 */
class AgentsPage(parameters: PageParameters? = null) : AuthBasePage(parameters) {

    @SpringBean
    private lateinit var agentService: SystemAgentService

    // TODO - общий класс для таблиц
    private var tableSizeNumber: Long = agentService.size()
    private var tableShowNumber: Integer =
            if (tableSizeNumber < 10) Integer(tableSizeNumber.toInt()) else Integer(10)
    private var updateTime: Date = Date(System.currentTimeMillis())

    private lateinit var tableNumberLabel: Label
    private lateinit var tableUpdateTime: Label
    private lateinit var tableShowNumberField: TextField<Integer>
    private lateinit var feedback: BootstrapFeedbackPanel
    private lateinit var listViewContainer: WebMarkupContainer

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        // <!-- Page level plugin CSS-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/datatables/dataTables.bootstrap4.css")))

        // <!-- Custom scripts for this page - index -->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin-datatables.min.js")))
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return HasAnyAuthorityPrincipalAcceptor(Authority.VIEW_OWN_AGENT, Authority.VIEW_ALL_AGENTS)
    }

    override fun onInitialize() {
        super.onInitialize()

        val showNumberModel = PropertyModel.of<Integer>(this, "tableShowNumber")
        val updateTimeModel = object : AbstractReadOnlyModel<String>() {
            override fun getObject(): String {
                return updateTime.toSqlite()
            }
        }

        tableNumberLabel = Label("tableNumberLabel", showNumberModel)
        tableUpdateTime = Label("tableUpdateTime", updateTimeModel)
        tableShowNumberField = TextField<Integer>("tableShowNumber", showNumberModel)
        tableShowNumberField.add(RangeValidator(1, tableSizeNumber.toInt()))

        feedback = BootstrapFeedbackPanel("feedback")
        val searchForm = Form<Void>("searchForm")
        searchForm.add(tableShowNumberField.setRequired(true).setOutputMarkupId(true))
        searchForm.add(object : AjaxSubmitLink("search") {
            override fun onSubmit(target: AjaxRequestTarget, form: Form<*>) {
                super.onSubmit(target, form)
                updateTable(target)
            }

            override fun onError(target: AjaxRequestTarget, form: Form<*>) {
                super.onError(target, form)
                target.add(feedback)
            }
        })
        listViewContainer = WebMarkupContainer("listViewContainer")
        listViewContainer.add(object : ListView<SystemAgent>("listView", configureAgentListModel()) {
            override fun populateItem(item: ListItem<SystemAgent>) {
                val agent = item.modelObject

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
                item.add(Label("createBy", PropertyModel.of<Long>(agent, "createUserId")))
                item.add(object : Label("createDate", PropertyModel.of<Date>(agent, "createDate")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return SqliteDateConverter() as IConverter<C>
                    }
                })
                item.add(object : Label("isDeleted", PropertyModel.of<Boolean>(agent, "isDeleted")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return BooleanYesNoConverter() as IConverter<C>
                    }
                })
            }
        })

        add(listViewContainer.setOutputMarkupId(true))
        add(feedback)
        add(searchForm)
        add(tableUpdateTime.setOutputMarkupId(true))
        add(tableNumberLabel.setOutputMarkupId(true))
        add(Label("tableSizeLabel", PropertyModel.of<Long>(this, "tableSizeNumber")))
    }

    private fun updateTable(target: AjaxRequestTarget) {
        updateTime = Date(System.currentTimeMillis())

        target.add(tableShowNumberField)
        target.add(tableUpdateTime)
        target.add(tableNumberLabel)
        target.add(listViewContainer)
        target.add(feedback)
    }

    private fun configureAgentListModel(): IModel<List<SystemAgent>> {
        return object : AbstractReadOnlyModel<List<SystemAgent>>() {
            override fun getObject(): List<SystemAgent> {
                return loadTableData()
            }
        }
    }

    /**
     * Обновление данных таблицы
     */
    private fun loadTableData(): List<SystemAgent> {
        return agentService.get(tableShowNumber.toLong())
    }

    private fun agentClick(agent: SystemAgent) {
        val parameters = PageParameters()
        parameters.set(AgentPage.AGENT_ID_PARAMETER_NAME, agent.id!!)
        setResponsePage(AgentPage::class.java, parameters)
    }
}