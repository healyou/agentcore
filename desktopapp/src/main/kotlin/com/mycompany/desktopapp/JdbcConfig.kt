package com.mycompany.desktopapp

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

/**
 * @author Nikita Gorodilov
 */
@Configuration
@EnableTransactionManagement
@PropertySource(value = ["classpath:context.properties"])
open class JdbcConfig {

    @Value("\${jdbc.driverClassName}")
    private lateinit var driverClassName: String
    @Value("\${jdbc.path}")
    private lateinit var path: String
    @Value("\${jdbc.username}")
    private lateinit var username: String
    @Value("\${jdbc.password}")
    private lateinit var password: String

    @Bean(name=["dataSource"])
    open fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driverClassName)
        dataSource.setUrl(path)
        dataSource.setUsername(username)
        dataSource.setPassword(password)
        return dataSource
    }

    @Bean(name=["jdbcTemplate"])
    open fun jdbcTemplate(@Qualifier("dataSource") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    @Bean(name=["transactionManager"])
    open fun transactionManager(@Qualifier("dataSource") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource);
    }
}