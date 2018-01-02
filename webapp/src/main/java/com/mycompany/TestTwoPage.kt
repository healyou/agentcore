package com.mycompany

import com.mycompany.security.acceptor.PrincipalAcceptor
import com.mycompany.user.Principal
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink

/**
 * @author Nikita Gorodilov
 */
class TestTwoPage : AuthBasePage() {

    override fun onInitialize() {
        super.onInitialize()
        add(object : AjaxLink<Void>("homePageLink") {
            override fun onClick(ajaxRequestTarget: AjaxRequestTarget) {
                setResponsePage(HomePage::class.java)
            }
        })
    }

    override fun getPrincipalAcceptor(): PrincipalAcceptor {
        return object : PrincipalAcceptor {
            override fun accept(principal: Principal): Boolean {
                return true
            }
        }
    }

    override fun getPageName(): String {
        return "TestTwoPage name"
    }
}
