package com.mycompany

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author Nikita Gorodilov
 */
@Configuration
@EnableScheduling
class ApplicationConfig: SchedulingConfigurer {

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor())
    }

    @Bean(destroyMethod = "shutdown")
    fun taskExecutor(): Executor {
        return Executors.newScheduledThreadPool(10)
    }
}