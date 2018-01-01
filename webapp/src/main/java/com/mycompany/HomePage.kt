package com.mycompany

import db.base.Environment
import db.core.sc.SystemAgentSC
import db.core.systemagent.SystemAgentService
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean

class HomePage(parameters: PageParameters? = null) : AuthBasePage(parameters) {

    @SpringBean
    lateinit var testBean: TestBean
    // TODO в пакете com.company находит jdbcTemplate
    @SpringBean
    lateinit var test: Environment
    // TODO вне пакете com.company jdbcTemplate не подставляет
    @SpringBean
    lateinit var test2: test

    override fun onInitialize() {
        super.onInitialize()

        add(Label("version", Model.of(testBean.getString2())/*getApplication().getFrameworkSettings().getVersion()*/))
        add(object : AjaxLink<Void>("testTwoPage") {
            override fun onClick(ajaxRequestTarget: AjaxRequestTarget) {
                setResponsePage(TestTwoPage::class.java)
            }
        })
        add(Label("test", Model.of("агент ${test.getProperty("agent.service.base.url")}")))
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return AlwaysAcceptedPrincipalAcceptor()
    }
}
