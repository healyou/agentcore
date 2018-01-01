package com.mycompany

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
open class test {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    fun test(): String {
        return "test"
    }
}