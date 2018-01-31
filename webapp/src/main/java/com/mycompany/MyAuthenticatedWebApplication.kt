package com.mycompany

import com.mycompany.agent.AgentPage
import com.mycompany.agent.AgentsPage
import com.mycompany.agent.monitoring.AgentsMonitoringPage
import org.apache.wicket.Page
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.spring.injection.annot.SpringComponentInjector

/**
 * @author Nikita Gorodilov
 */
open class MyAuthenticatedWebApplication : AuthenticatedWebApplication() {

    override fun init() {
        super.init()
        componentInstantiationListeners.add(SpringComponentInjector(this))
        debugSettings.isDevelopmentUtilitiesEnabled = true

        mountPage("/login", LoginPage::class.java)
        mountPage("/logout", LogoutPage::class.java)
        mountPage("/agents", AgentsPage::class.java)
        mountPage("/agent", AgentPage::class.java)
        mountPage("/monitoring", AgentsMonitoringPage::class.java)
    }

    override fun getWebSessionClass(): Class<out AbstractAuthenticatedWebSession> {
        return SpringAuthenticatedWebSession::class.java
    }

    override fun getSignInPageClass(): Class<out WebPage> {
        return LoginPage::class.java
    }

    override fun getHomePage(): Class<out Page> {
        return HomePage::class.java
    }
}
