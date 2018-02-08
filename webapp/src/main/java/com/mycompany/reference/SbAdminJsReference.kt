package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.JavaScriptHeaderItem
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.request.resource.JavaScriptResourceReference

/**
 * @author Nikita Gorodilov
 */
class SbAdminJsReference :
        JavaScriptResourceReference(HomePage::class.java, "resource/js/sb-admin.js") {

    companion object {
        private val INSTANCE = SbAdminJsReference()

        fun get(): SbAdminJsReference {
            return INSTANCE
        }

        fun headerItem(): JavaScriptReferenceHeaderItem {
            return JavaScriptHeaderItem.forReference(get())
        }
    }
}