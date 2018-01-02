package com.mycompany.base.converter

import com.mycompany.db.base.toSqlite
import org.apache.wicket.util.convert.IConverter
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class SqliteDateConverter: IConverter<Date> {

    override fun convertToString(value: Date?, locale: Locale): String {
        if (value != null) {
            return value.toSqlite()
        }

        return ""
    }

    override fun convertToObject(value: String, locale: Locale): Date? {
        return null
    }
}