package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
class SbAdminCssReference :
        CssResourceReference(HomePage::class.java, "resource/css/sb-admin.css") {

    companion object {
        private val INSTANCE = SbAdminCssReference()

        fun get(): SbAdminCssReference {
            return INSTANCE
        }

        fun headerItem(): CssHeaderItem {
            return CssHeaderItem.forReference(get())
        }
    }
}