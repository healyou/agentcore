package com.mycompany.db.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author Nikita Gorodilov
 */
abstract class AbstractDao {

    @Autowired
    protected open lateinit var jdbcTemplate: JdbcTemplate

    /**
     * Возвращает идентификатор последней введённой в таблицу записи
     *
     * @param tableName Имя таблицы в бд
     */
    protected fun getSequence(tableName: String) : Long {
        return jdbcTemplate.queryForObject("select seq from sqlite_sequence where name='$tableName'", Long::class.java)
    }
}