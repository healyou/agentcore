package com.mycompany.reference

import com.mycompany.HomePage
import org.apache.wicket.markup.head.CssHeaderItem
import org.apache.wicket.request.resource.CssResourceReference

/**
 * @author Nikita Gorodilov
 */
class DataTablesCssReference :
        CssResourceReference(HomePage::class.java, "resource/vendor/datatables/dataTables.bootstrap4.css") {

    companion object {
        private val INSTANCE = DataTablesCssReference()

        fun get(): DataTablesCssReference {
            return INSTANCE
        }

        fun headerItem(): CssHeaderItem {
            return CssHeaderItem.forReference(get())
        }
    }
}