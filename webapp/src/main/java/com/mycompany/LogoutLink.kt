package com.mycompany

import org.apache.wicket.Application
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink

/**
 * Ссылка выхода из приложения
 *
 * @author Nikita Gorodilov
 */
class LogoutLink(id: String): AjaxLink<Any>(id) {

    override fun onClick(target: AjaxRequestTarget) {
        session.invalidate()
        (Application.get() as MyAuthenticatedWebApplication).restartResponseAtSignInPage()
    }
}