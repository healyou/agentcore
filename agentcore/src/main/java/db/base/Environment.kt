package db.base

import org.springframework.stereotype.Component

/**
 * Получение параметров из бд
 *
 * @author Nikita Gorodilov
 */
@Component
open class Environment : AbstractDao() {

    fun getProperty(key: String): String {
        return jdbcTemplate.queryForObject("select value from parameter where key = ?", String::class.java, key)
    }

    fun addProperty(key: String, value: String): Long {
        // todo вынести в AbstractDao c возвратом id
        jdbcTemplate.update("INSERT INTO parameter " +
                "(key, value) VALUES (?, ?);",
                key,
                value
        )

        /* id последней введённой записи */
        return jdbcTemplate.queryForObject("select seq from sqlite_sequence where name='parameter';", Long::class.java)
    }
}