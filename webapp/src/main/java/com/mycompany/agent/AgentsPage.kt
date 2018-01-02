package com.mycompany.agent

import com.mycompany.*
import com.mycompany.db.base.toSqlite
import com.mycompany.security.acceptor.HasAnyAuthorityPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference
import org.apache.wicket.validation.validator.RangeValidator
import java.util.*

/**
 * Реестр агентов
 *
 * @author Nikita Gorodilov
 */
class AgentsPage(parameters: PageParameters? = null) : AuthBasePage(parameters) {

    // TODO - общий класс для таблиц
    private var tableShowNumber: Integer = Integer(10)
    private var tableSizeNumber: Long = 100
    private var updateTime: Date = Date(System.currentTimeMillis())

    private lateinit var tableNumberLabel: Label
    private lateinit var tableUpdateTime: Label
    private lateinit var tableShowNumberField: TextField<Integer>
    private lateinit var feedback: BootstrapFeedbackPanel

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

    // todo загрузка самих данных через listview

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
        searchForm.add(tableShowNumberField.setOutputMarkupId(true))
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
        target.add(feedback)
    }
}