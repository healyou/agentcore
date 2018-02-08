package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * @author Nikita Gorodilov
 */
class BootstrapJsReference :
        JavaScriptResourceReference(HomePage::class.java, "resource/vendor/bootstrap/js/bootstrap.bundle.min.js") {

    companion object {
        private val INSTANCE = BootstrapJsReference()

        fun get(): BootstrapJsReference {
            return INSTANCE
        }

        fun headerItem(): JavaScriptReferenceHeaderItem {
            return JavaScriptHeaderItem.forReference(get())
        }
    }
}