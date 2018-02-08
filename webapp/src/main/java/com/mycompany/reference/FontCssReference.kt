package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
class FontCssReference :
        CssResourceReference(HomePage::class.java, "resource/vendor/font-awesome/css/font-awesome.min.css") {

    companion object {
        private val INSTANCE = FontCssReference()

        fun get(): FontCssReference {
            return INSTANCE
        }

        fun headerItem(): CssHeaderItem {
            return CssHeaderItem.forReference(get())
        }
    }
}