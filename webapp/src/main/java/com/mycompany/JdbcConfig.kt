package com.mycompany

import org.springframework.context.annotation.*
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
//    open val dataSource: DataSource
//        @Bean
//        get() {
//            val dsLookup = JndiDataSourceLookup()
//            dsLookup.isResourceRef = true
//            return dsLookup.getDataSource("jdbc/agetwebappDS")
//        }
//
//    open val jdbcTemplate: JdbcTemplate
//        @Bean
//        get() {
//            val jdbcTemplate = JdbcTemplate()
//            jdbcTemplate.dataSource = dataSource
//            return jdbcTemplate
//        }
    @Bean("jdbcTemplate")
    @Throws(Exception::class)
    fun jdbcTemplate(): JdbcTemplate {
        val jdbcTemplate = JdbcTemplate()
        jdbcTemplate.dataSource = dataSource()
        return jdbcTemplate
    }

//    open val transactionManager: PlatformTransactionManager
//        @Bean
//        get() {
//            return DataSourceTransactionManager(dataSource)
//        }

    @Bean
    @Throws(Exception::class)
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource())
    }
}
