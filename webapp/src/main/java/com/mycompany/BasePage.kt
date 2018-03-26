package com.mycompany

import com.mycompany.agent.AgentsPage
import com.mycompany.agent.monitoring.AgentsMonitoringPage
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.reference.*
import com.mycompany.security.PrincipalSupport
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.OnDomReadyHeaderItem
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * @author Nikita Gorodilov
 */
abstract class BasePage(parameters: PageParameters? = null) : WebPage(parameters), PrincipalSupport {

    // todo sheduler веб приложения не закрывается после exit
    override fun onInitialize() {
        super.onInitialize()
        add(LogoutLink("logout"))
        add(Label("pageName", Model.of(getPageName())))
        add(object : AjaxLink<Any>("homePage") {
            override fun onClick(target: AjaxRequestTarget) {
                setResponsePage(application.homePage)
            }
        })
        add(object : AjaxLambdaLink<Any>("agentsPage", this::agentsPageClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isPrincipalHasAnyAuthority(Authority.VIEW_OWN_AGENT, Authority.VIEW_ALL_AGENTS)
            }
        })
        add(object : AjaxLambdaLink<Any>("monitoringPage", this::monitoringPageClick) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isPrincipalHasAnyAuthority(Authority.VIEW_OWN_AGENT)
            }
        })
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)

        response.render(BootstrapCssReference.headerItem())
        response.render(FontCssReference.headerItem())
        response.render(DataTablesCssReference.headerItem())
        response.render(SbAdminCssReference.headerItem())

        response.render(BootstrapJsReference.headerItem())
        response.render(JQueryEasingJsReference.headerItem())
        response.render(SbAdminJsReference.headerItem())

        // todo - что-то не так с подключением файлов
        response.render(OnDomReadyHeaderItem.forScript(
                "// Toggle the side navigation\n" +
                "  \$(\"#sidenavToggler\").click(function(e) {\n" +
                "    e.preventDefault();\n" +
                "    \$(\"body\").toggleClass(\"sidenav-toggled\");\n" +
                "    \$(\".navbar-sidenav .nav-link-collapse\").addClass(\"collapsed\");\n" +
                "    \$(\".navbar-sidenav .sidenav-second-level, .navbar-sidenav .sidenav-third-level\").removeClass(\"show\");\n" +
                "  });"
        ))
    }

    protected open fun getPageName(): String {
        return getString("pageName")
    }

    private fun agentsPageClick(target: AjaxRequestTarget) {
        setResponsePage(AgentsPage::class.java)
    }

    private fun monitoringPageClick(target: AjaxRequestTarget) {
        setResponsePage(AgentsMonitoringPage::class.java)
    }
}
