package com.mycompany.agent.monitoring

import com.mycompany.AuthBasePage
import com.mycompany.BootstrapFeedbackPanel
import com.mycompany.agent.AgentPage
import com.mycompany.agent.panels.AgentEventHistoryPanel
import com.mycompany.agent.panels.ServiceMessagesPanel
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.base.converter.BooleanYesNoConverter
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.*
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.util.convert.IConverter


/**
 * Страница мониторинга состояния агентов(только свои агенты)
 *
 * @author Nikita Gorodilov
 */
class AgentsMonitoringPage : AuthBasePage() {

    companion object {
        private val TABLE_MAX_SHOW_SIZE = 10
        private val MODAL_COOKIE_NAME = "modal" + this::class.java.name
        private val SHOW_LAST_SYSTEM_MESSAGES_NUMBER = 10
        private val SHOW_LAST_EVENT_HISTORY_NUMBER = 10
    }

    @SpringBean
    private lateinit var agentService: SystemAgentService
    @SpringBean
    private lateinit var serviceMessageService: ServiceMessageService
    @SpringBean
    private lateinit var eventHistoryService: SystemAgentEventHistoryService
    @SpringBean
    private lateinit var agentWorkControl: IRuntimeAgentWorkControl

    // TODO - общий класс для таблиц
    /* Количество отображаемых объектов */
    private var tableSizeNumber: Long = agentService.size(getPrincipal().user.id!!)
    private var tableShowNumber: Integer =
            if (tableSizeNumber < TABLE_MAX_SHOW_SIZE) Integer(tableSizeNumber.toInt()) else Integer(TABLE_MAX_SHOW_SIZE)

    private lateinit var feedback: BootstrapFeedbackPanel
    private lateinit var buttons: WebMarkupContainer
    private lateinit var listViewContainer: WebMarkupContainer
    private lateinit var tableNumberLabel: Label
    private lateinit var modal: ModalWindow

    /* Выделение объектов в таблице */
    private val checkedAgentsIds = HashSet<Long>()
    private var searchResult: List<SystemAgent> = arrayListOf()

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return HasAnyAuthorityPrincipalAcceptor(Authority.VIEW_OWN_AGENT)
    }

    override fun onInitialize() {
        super.onInitialize()

        modal = object : ModalWindow("modal") {
            override fun showUnloadConfirmation(): Boolean {
                return false
            }
        }
        add(modal)
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

        buttons = WebMarkupContainer("buttons")
        add(buttons.setOutputMarkupId(true))
        buttons.add(object : AjaxLambdaLink<Any>("agentMonitoringButton", this::agentMonitoringButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = checkedAgentsIds.size == 1
            }
        })

        val agentShowDataButtons = object : WebMarkupContainer("agentShowDataButtons") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = checkedAgentsIds.size == 1
            }
        }
        buttons.add(agentShowDataButtons)
        agentShowDataButtons.add(AjaxLambdaLink<Any>("showServiceMessages", this::showServiceMessagesClick))
        agentShowDataButtons.add(AjaxLambdaLink<Any>("showEventHistory", this::showEventHistoryClick))

        val agentWorkButtons = object : WebMarkupContainer("agentWorkButtons") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isPrincipalHasAnyAuthority(Authority.STARTED_OWN_AGENT)
            }
        }
        buttons.add(agentWorkButtons)
        agentWorkButtons.add(object : AjaxLambdaLink<Any>("start", this::startButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isStartAllCheckedAgents()
            }
        })
        agentWorkButtons.add(object : AjaxLambdaLink<Any>("stop", this::stopButtonClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isStopAllCheckedAgents()
            }
        })
    }

    /**
     * Выбран 1 агент и он работает
     */
    private fun isCheckOneStartedAgent(): Boolean {
        return checkedAgentsIds.size == 1 &&
                agentWorkControl.isStarted(getAgent(checkedAgentsIds.elementAt(0)))
    }

    /**
     * Все выбранные агенты могут завершить работу
     */
    private fun isStopAllCheckedAgents(): Boolean {
        return checkedAgentsIds.all { agentId ->
            val agent = getAgent(agentId)
            return agentWorkControl.isStarted(agent)
        } && checkedAgentsIds.isNotEmpty()
    }

    /**
     * Все выбранные агенты могут начать работу
     */
    private fun isStartAllCheckedAgents(): Boolean {
        return checkedAgentsIds.all { agentId ->
            val agent = getAgent(agentId)
            return agentWorkControl.isStart(agent)
        } && checkedAgentsIds.isNotEmpty()
    }

    private fun agentMonitoringButtonClick(target: AjaxRequestTarget) {
        val parameters = PageParameters()
        parameters.set(AgentMonitoringPage.AGENT_MONITORING_PAGE_AGENT_ID_PARAMETER_NAME, getAgentIdIfOneCheckedOrThrowError())
        setResponsePage(AgentMonitoringPage::class.java, parameters)
    }

    /**
     * Получить id одного выбранного агента, если агент выбран не один - выдать ошибку
     */
    @Throws(RuntimeException::class)
    private fun getAgentIdIfOneCheckedOrThrowError(): Long {
        if (checkedAgentsIds.size != 1) {
            throw RuntimeException("Выбран не 1 агент")
        }
        return checkedAgentsIds.elementAt(0)
    }

    /**
     * Можно открыть, если выделен 1 агент
     */
    private fun showServiceMessagesClick(target: AjaxRequestTarget) {
        modal.setContent(ServiceMessagesPanel(modal.contentId, configureServiceMessagesModel()))
        modal.setTitle(getString("serviceMessageModalName"))
        modal.cookieName = MODAL_COOKIE_NAME
        modal.show(target)
    }

    private fun configureServiceMessagesModel(): IModel<List<ServiceMessage>> {
        return object : LoadableDetachableModel<List<ServiceMessage>>() {
            override fun load(): List<ServiceMessage> {
                /* Последние сообщения агента */
                val checkedAgentId = getAgentIdIfOneCheckedOrThrowError()
                return serviceMessageService.getLastNumberItems(checkedAgentId, SHOW_LAST_SYSTEM_MESSAGES_NUMBER.toLong())
                        .asReversed()
            }
        }
    }

    /**
     * Можно открыть, если выделен 1 агент
     */
    private fun showEventHistoryClick(target: AjaxRequestTarget) {
        modal.setContent(AgentEventHistoryPanel(modal.contentId, configureEventHistoryModel()))
        modal.setTitle(getString("eventHistoryModalName"))
        modal.cookieName = MODAL_COOKIE_NAME
        modal.show(target)
    }

    private fun configureEventHistoryModel(): IModel<List<SystemAgentEventHistory>> {
        return object : LoadableDetachableModel<List<SystemAgentEventHistory>>() {
            override fun load(): List<SystemAgentEventHistory> {
                /* Последние действия агента */
                val agentId = checkedAgentsIds.elementAt(0)
                return eventHistoryService.getLastNumberItems(agentId, SHOW_LAST_EVENT_HISTORY_NUMBER.toLong())
                        .asReversed()
            }
        }
    }

    /**
     * Начало работы агентов
     */
    private fun startButtonClick(target: AjaxRequestTarget) {
        getCheckedAgents().forEach {
            agentWorkControl.start(it)
        }
        checkedAgentsIds.clear()
        updatePage(target)
    }

    /**
     * Конец работы агентов
     */
    private fun stopButtonClick(target: AjaxRequestTarget) {
        getCheckedAgents().forEach {
            agentWorkControl.stop(it)
        }
        checkedAgentsIds.clear()
        updatePage(target)
    }

    private fun updatePage(target: AjaxRequestTarget) {
        target.add(listViewContainer)
        target.add(buttons)
    }

    /**
     * Список всех выбранных агентов
     */
    private fun getCheckedAgents(): List<SystemAgent> {
        return checkedAgentsIds.map {
            getAgent(it)
        }
    }

    /**
     * Текст указывающий текущий статус агента
     */
    private fun configureAgentStatusLabel(agent: SystemAgent): String {
        return if (agentWorkControl.isStarted(agent)) "Работает" else "Не запущен"
    }

    private fun configureAgentListModel(): IModel<List<SystemAgent>> {
        return object : AbstractReadOnlyModel<List<SystemAgent>>() {
            override fun getObject(): List<SystemAgent> {
                return loadTableData()
            }
        }
    }

    private fun loadTableData(): List<SystemAgent> {
        searchResult = agentService.get(tableShowNumber.toLong(), getPrincipal().user.id!!)
        return searchResult
    }

    private fun checkAgent(target: AjaxRequestTarget, agent: SystemAgent, check: Boolean) {
        if (check) {
            checkedAgentsIds.add(agent.id!!)
        } else {
            checkedAgentsIds.remove(agent.id!!)
        }

        target.add(buttons)
    }

    private fun agentClick(agent: SystemAgent) {
        val parameters = PageParameters()
        parameters.set(AgentPage.AGENT_PAGE_AGENT_ID_PARAMETER_NAME, agent.id!!)
        setResponsePage(AgentPage::class.java, parameters)
    }

    /**
     * Выбран ли агент на странице
     */
    private fun isCheck(agent: SystemAgent): Boolean {
        return checkedAgentsIds.contains(agent.id!!)
    }

    /**
     * Поиск выделенного на странице агента(его может и не быть на странице)
     */
    private fun getAgent(id: Long): SystemAgent {
        val agents = searchResult.filter { it.id == id }
        if (!agents.isEmpty()) {
            return agents[0]
        }

        throw RuntimeException("Агента с id = $id не найден на странице")
    }
}