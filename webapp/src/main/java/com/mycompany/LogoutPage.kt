package com.mycompany

import org.apache.wicket.Application
import org.apache.wicket.markup.html.WebPage

/**
 * @author Nikita Gorodilov
 */
class LogoutPage : WebPage() {

    override fun onInitialize() {
        super.onInitialize()

        session.invalidate()
        setResponsePage(application.homePage)
    }
}
