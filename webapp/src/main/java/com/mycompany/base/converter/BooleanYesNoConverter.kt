package com.mycompany.base.converter

import org.apache.wicket.util.convert.IConverter
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class BooleanYesNoConverter: IConverter<Boolean> {

    private val YES = "да"
    private val NO = "нет"

    override fun convertToString(value: Boolean?, locale: Locale): String {
        if (value != null) {
            return if (java.lang.Boolean.TRUE == value) YES else NO
        }

        return NO
    }

    override fun convertToObject(value: String, locale: Locale): Boolean? {
        return null
    }
}