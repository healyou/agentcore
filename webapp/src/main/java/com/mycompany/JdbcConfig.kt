package com.mycompany

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


/**
 * @author Nikita Gorodilov
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.mycompany")
class JdbcConfig {

    @Bean
    @Throws(Exception::class)
    fun dataSource(): DataSource {
        val dsLookup = JndiDataSourceLookup()
        dsLookup.isResourceRef = true
        return dsLookup.getDataSource("jdbc/agetwebappDS")
    }

    @Bean(name = arrayOf("jdbcTemplate"))
    @Throws(Exception::class)
    fun jdbcTemplate(): JdbcTemplate {
        val jdbcTemplate = JdbcTemplate()
        jdbcTemplate.dataSource = dataSource()
        return jdbcTemplate
    }

    @Bean
    @Throws(Exception::class)
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource())
    }
}
