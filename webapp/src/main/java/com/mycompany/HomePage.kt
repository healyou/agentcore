package com.mycompany

import com.mycompany.db.base.Environment
import com.mycompany.security.acceptor.AlwaysAcceptedPrincipalAcceptor
import com.mycompany.security.acceptor.PrincipalAcceptor
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.spring.injection.annot.SpringBean

class HomePage(parameters: PageParameters? = null) : AuthBasePage(parameters) {

    @SpringBean
    lateinit var testBean: TestBean
    @SpringBean
    lateinit var test: Environment

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
