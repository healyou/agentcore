package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
class BootstrapCssReference :
        CssResourceReference(HomePage::class.java, "resource/vendor/bootstrap/css/bootstrap.min.css") {

    companion object {
        private val INSTANCE = BootstrapCssReference()

        fun get(): BootstrapCssReference {
            return INSTANCE
        }

        fun headerItem(): CssHeaderItem {
            return CssHeaderItem.forReference(get())
        }
    }
}