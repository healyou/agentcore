package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * @author Nikita Gorodilov
 */
class JQueryJsReference :
        JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery/jquery.min.js") {

    companion object {
        private val INSTANCE = JQueryJsReference()

        fun get(): JQueryJsReference {
            return INSTANCE
        }

        fun headerItem(): JavaScriptReferenceHeaderItem {
            return JavaScriptHeaderItem.forReference(get())
        }
    }
}