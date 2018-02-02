package com.mycompany

import com.mycompany.service.tasks.ServiceTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.*
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
@PropertySource("classpath:testJdbc.properties")
@ComponentScan(
        basePackages= ["com.mycompany"]
        , excludeFilters=[
            @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=ServiceTask.class)
        ])
public class JdbcTestConfig {

    @Value("\${jdbc.driverClassName}")
    public String driverClassName
    @Value("\${jdbc.path}")
    public String path
    @Value("\${jdbc.username}")
    public String username
    @Value("\${jdbc.password}")
    public String password

    @Bean(name="dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName)
        dataSource.setUrl(path)
        dataSource.setUsername(username)
        dataSource.setPassword(password)
        return dataSource
    }

    @Bean(name="jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource)
    }

    @Bean(name="transactionManager")
    @Autowired
    PlatformTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
