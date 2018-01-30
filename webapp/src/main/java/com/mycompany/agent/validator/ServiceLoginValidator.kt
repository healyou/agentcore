package com.mycompany.agent.validator

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import org.apache.wicket.injection.Injector
import org.apache.wicket.model.IModel
import org.apache.wicket.spring.injection.annot.SpringBean
import org.apache.wicket.validation.IValidatable
import org.apache.wicket.validation.IValidator
import org.apache.wicket.validation.ValidationError

/**
 * Валидация логина агента(должен быть уникален при создании(todo серверная проверка))
 * Поиск текста ошибки - ServiceLoginValidator + .key
 * todo - test
 * @author Nikita Gorodilov
 */
class ServiceLoginValidator(private val model: IModel<SystemAgent>): IValidator<String> {

    @SpringBean
    private lateinit var agentService: SystemAgentService

    init {
        Injector.get().inject(this)
    }

    override fun validate(validatable: IValidatable<String>) {
        val login = validatable.value
        if (model.`object`.isNew && agentService.isExistsAgent(login)) {
            validatable.error(ValidationError().addKey(javaClass.simpleName + ".incorrectServiceLogin"))
        }
    }
}