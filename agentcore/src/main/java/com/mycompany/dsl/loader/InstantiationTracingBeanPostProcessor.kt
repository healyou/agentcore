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

    // simply return the instantiated bean as-is
    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String?): Any? {
        return bean // we could potentially return any object reference here...
    }

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String?): Any? {
        if (beanName == "runtimeAgentWorkControl") {
            runtimeAgentLoader = bean
        }
        println("Bean '" + beanName + "' created : " + bean.toString())
        return bean
    }
}