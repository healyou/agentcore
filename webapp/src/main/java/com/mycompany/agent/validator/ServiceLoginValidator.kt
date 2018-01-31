package com.mycompany.agent.validator

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.service.ServerAgentService
import com.mycompany.service.SessionManager
import org.apache.wicket.injection.Injector
import org.apache.wicket.model.IModel
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.validation.IValidatable
import org.apache.wicket.validation.IValidator
import org.apache.wicket.validation.ValidationError

/**
 * Валидация логина агента(в сервисе - masId, должен быть уникален при создании)
 * Поиск текста ошибки - ServiceLoginValidator + .key
 *
 * @author Nikita Gorodilov
 */
class ServiceLoginValidator(private val model: IModel<SystemAgent>): IValidator<String> {

    @SpringBean
    private lateinit var systemAgentService: SystemAgentService
    @SpringBean
    private lateinit var serviceAgentService: ServerAgentService

    init {
        Injector.get().inject(this)
    }

    override fun validate(validatable: IValidatable<String>) {
        val agentServiceLogin = validatable.value

        if (isExistsLocalAgent(agentServiceLogin)) {
            validatable.error(ValidationError().addKey(javaClass.simpleName + ".incorrectServiceLogin.local"))
        }

        val isExistsServiceAgent = isExistsServiceAgent(agentServiceLogin)
        if (isExistsServiceAgent == null) {
            validatable.error(ValidationError().addKey(javaClass.simpleName + ".serviceNotFound"))
        } else if (isExistsServiceAgent) {
            validatable.error(ValidationError().addKey(javaClass.simpleName + ".incorrectServiceLogin.service"))
        }
    }

    /**
     * Существование агента в локальной бд
     */
    private fun isExistsLocalAgent(serviceLogin: String): Boolean {
        return model.`object`.isNew && systemAgentService.isExistsAgent(serviceLogin)
    }

    /**
     * Существование агента в сервисе
     */
    private fun isExistsServiceAgent(serviceLogin: String): Boolean? {
        return serviceAgentService.isExistsAgent(SessionManager(), serviceLogin)
    }
}