package com.mycompany

import com.mycompany.db.base.Environment
import org.apache.wicket.spring.injection.annot.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * @author Nikita Gorodilov
 */
@Component
class TestBean {

    @Autowired
    lateinit var test: Environment

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    fun getString(): String {
        return "ggwp bean"
    }

    fun getString2(): String? {
        return jdbcTemplate.queryForObject("select name from service_message_type limit 1", String::class.java)
    }
}
