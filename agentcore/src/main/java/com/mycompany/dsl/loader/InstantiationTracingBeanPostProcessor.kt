package com.mycompany.dsl.loader

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component


/**
 * @author Nikita Gorodilov
 */
// todo в другой пакет
@Component
class InstantiationTracingBeanPostProcessor: BeanPostProcessor {

    companion object {
        lateinit var runtimeAgentLoader: Any
    }

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String?): Any? {
        return bean
    }

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String?): Any? {
        // todo класс
        if (beanName == "runtimeAgentWorkControl") {
            runtimeAgentLoader = bean
        }
        if (beanName == "jdbcTemplate") {
            println("jdbcTemplate create bean!!!")
        }

        return bean
    }
}