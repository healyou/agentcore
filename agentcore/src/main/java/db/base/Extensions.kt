package db.base

import db.base.Codable
import service.objects.AgentType
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Nikita Gorodilov
 */
val SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"

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
    if (this == "N" || this == "Y") {
        return this != "N"
    }
    else {
        throw UnsupportedOperationException("Нельзя перевести строку в isDeleted: Boolean")
    }
}

/* из объекта в sqlite */
fun Boolean.toSqlite(): String {
    return if (this) {
        "Y"
    } else {
        "N"
    }
}

/********* agentCodes *********/
// TODO тесты для этого - работает неправильно
/* из sqlite в объект */
fun String.sqlite_toAgentCodes(): List<AgentType.Code> {
    val codes = this.split("!")

    val list = arrayListOf<AgentType.Code>()
    codes.forEach {
        if (it.isNotEmpty()) {
            list.add(Codable.find(AgentType.Code::class.java, it))
        }
    }

    return list
}

/* из объекта в sqlite */
fun List<AgentType.Code>.toSqlite(): String {
    var codesString = ""

    this
            .map { it.code }
            .forEach { codesString = codesString.plus(it + "!") }

    if (codesString.isNotEmpty() && codesString[codesString.length - 1] == '!') {
        codesString = codesString.subSequence(0, codesString.length - 1).toString()
    }

    return codesString
}