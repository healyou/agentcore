package com.mycompany

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author Nikita Gorodilov
 */
@Configuration
@EnableScheduling
@ComponentScan("com.mycompany")
class ApplicationConfig {
}