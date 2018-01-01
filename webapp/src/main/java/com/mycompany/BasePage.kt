package com.mycompany

import org.apache.wicket.injection.Injector
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.mapper.parameter.PageParameters
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
abstract class BasePage(parameters: PageParameters? = null) : WebPage(parameters) {

    override fun renderHead(response: IHeaderResponse) {
        response.render(CssHeaderItem.forReference(CssResourceReference(HomePage::class.java, "resource/css/style.css")))
    }
}
