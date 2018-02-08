package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * @author Nikita Gorodilov
 */
class JQueryEasingJsReference :
        JavaScriptResourceReference(HomePage::class.java, "resource/vendor/jquery-easing/jquery.easing.min.js") {

    companion object {
        private val INSTANCE = JQueryEasingJsReference()

        fun get(): JQueryEasingJsReference {
            return INSTANCE
        }

        fun headerItem(): JavaScriptReferenceHeaderItem {
            return JavaScriptHeaderItem.forReference(get())
        }
    }
}