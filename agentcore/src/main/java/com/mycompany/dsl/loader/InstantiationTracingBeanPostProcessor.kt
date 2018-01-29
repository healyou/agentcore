package com.mycompany.dsl.loader

import com.mycompany.agentworklibrary.ILibraryAgentWorkControl
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component


/**
 * @author Nikita Gorodilov
 */
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
        if (bean is ILibraryAgentWorkControl && beanName == "runtimeAgentWorkControl") {
            runtimeAgentLoader = bean
        }

        return bean
    }
}