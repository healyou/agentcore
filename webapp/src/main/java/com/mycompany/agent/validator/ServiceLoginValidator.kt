package com.mycompany.agent.validator

import com.mycompany.db.core.systemagent.SystemAgentService
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.spring.injection.annot.SpringComponentInjector
import org.apache.wicket.validation.IValidatable
import org.apache.wicket.validation.IValidator
import org.apache.wicket.validation.ValidationError

/**
 * Валидация логина агента(должен быть уникален)
 * Поиск текста ошибки - ServiceLoginValidator + .key
 *
 * @author Nikita Gorodilov
 */
class ServiceLoginValidator: IValidator<String> {

    @SpringBean
    private lateinit var agentService: SystemAgentService

    init {
        SpringComponentInjector.get().inject(this)
    }

    override fun validate(validatable: IValidatable<String>) {
        val login = validatable.value
        if (agentService.isExistsAgent(login)) {
            validatable.error(ValidationError().addKey(javaClass.simpleName + ".incorrectServiceLogin"))
        }
    }
}