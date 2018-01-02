package com.mycompany

import com.mycompany.agent.AgentsPage
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.security.PrincipalSupport
import com.mycompany.user.Authority
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * @author Nikita Gorodilov
 */
abstract class BasePage(parameters: PageParameters? = null) : WebPage(parameters), PrincipalSupport {

    // TODO - класс на каждый из Reference
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
    }

    override fun renderHead(response: IHeaderResponse) {
        //response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/css/style.css")))
        // <!-- Bootstrap core CSS-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/bootstrap/css/bootstrap.min.css")))
        // <!-- Custom fonts for this template -->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/font-awesome/css/font-awesome.min.css")))
        // <!-- Page level plugin CSS-->
        //response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/vendor/datatables/dataTables.bootstrap4.css")))
        // <!-- Custom styles for this template-->
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/css/sb-admin.css")))

        // <!-- Bootstrap core JavaScript-->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery/jquery.min.js")))
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/bootstrap/js/bootstrap.bundle.min.js")))
        // <!-- Core plugin JavaScript-->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery-easing/jquery.easing.min.js")))
        // <!-- Page level plugin JavaScript-->
        //response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/chart.js/Chart.min.js")))
        //response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/datatables/jquery.dataTables.js")))
        //response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/vendor/datatables/dataTables.bootstrap4.js")))
        // <!-- Custom scripts for all pages-->
        response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin.min.js")))
        // <!-- Custom scripts for this page - index -->
        //response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin-datatables.min.js")))
        //response.render(JavaScriptHeaderItem.forReference(JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin-charts.min.js")))
    }

    protected open fun getPageName(): String {
        return getString("pageName")
    }

    private fun agentsPageClick(target: AjaxRequestTarget) {
        setResponsePage(AgentsPage::class.java)
    }
}
