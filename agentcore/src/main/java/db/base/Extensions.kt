package db.base

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Nikita Gorodilov
 */
/* Формат даты, хранимой в бд */
val SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
val SQLITE_YES_STRING = "Y"
val SQLITE_NO_STRING = "N"
val SQLITE_SPLIT_SIMBOL = '!'


/********* дата *********/
/* дату в строку sqlite */
fun Date.toSqlite(): String {
    return SimpleDateFormat(SQLITE_DATE_FORMAT).format(this)
}

/* из sqlite в строку */
fun String.fromSqlite(): Date {
    return SimpleDateFormat(SQLITE_DATE_FORMAT).parse(this)
}


/********* isDeleted *********/
/* из sqlite в объект */
fun String.sqlite_toBoolean(): Boolean {
    if (this == SQLITE_NO_STRING || this == SQLITE_YES_STRING) {
        return this != SQLITE_NO_STRING
    }
    else {
        throw UnsupportedOperationException("Нельзя перевести строку в isDeleted: Boolean")
    }
}

/* из объекта в sqlite string*/
fun Boolean.toSqlite(): String {
    return if (this) {
        SQLITE_YES_STRING
    } else {
        SQLITE_NO_STRING
    }
}


/********* agentCodes *********/
/* из sqlite в объект */
fun String.sqlite_toAgentCodes(): List<String> {
    if (this.isEmpty()) {
        return arrayListOf()
    }
    return this.split("!")
}

/* из объекта в sqlite */
fun List<String>.toSqlite(): String {
    var codesString = ""

    this.forEach { codesString = codesString.plus(it + SQLITE_SPLIT_SIMBOL) }

    if (codesString.isNotEmpty() && codesString[codesString.length - 1] == SQLITE_SPLIT_SIMBOL) {
        codesString = codesString.subSequence(0, codesString.length - 1).toString()
    }

    return codesString
}