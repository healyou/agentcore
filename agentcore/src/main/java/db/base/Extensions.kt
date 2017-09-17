package db.base

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Nikita Gorodilov
 */
val SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"

/********* дата *********/
// todo делать работу с message - ну и тесты для них -> продумать сразу всю работу с сообщениями в режиме сервиса и тесты для них делать
// todo запись ms времени -> так-то не так и надо
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
fun String.toIsDeleted(): Boolean {
    if (this == "N" || this == "Y") {
        return this != "N"
    }
    else {
        throw UnsupportedOperationException("Нельзя перевести строку в isDeleted: Boolean")
    }
}

/* из объекта в sqlite */
fun Boolean.toSqlite(): String {
    if (this) {
        return "Y"
    }
    else {
        return "N"
    }
}