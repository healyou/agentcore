package com.mycompany.db.base

import org.springframework.stereotype.Component

/**
 * Получение параметров из бд
 *
 * @author Nikita Gorodilov
 */
@Component
open class Environment : AbstractDao() {

    open fun getProperty(key: String): String {
        return jdbcTemplate.queryForObject("select value from parameter where key = ?", String::class.java, key)
    }

    fun addProperty(key: String, value: String): Long {
        jdbcTemplate.update("INSERT INTO parameter " +
                "(key, value) VALUES (?, ?);",
                key,
                value
        )

        /* id последней введённой записи */
        return getSequence("parameter")
    }
}