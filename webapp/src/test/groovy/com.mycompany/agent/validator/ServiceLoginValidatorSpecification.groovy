package com.mycompany.agent.validator

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.StringObjects
import com.mycompany.objects.SystemAgentObjects
import org.apache.wicket.model.Model
import org.apache.wicket.validation.Validatable

/**
 * @author Nikita Gorodilov
 */
class ServiceLoginValidatorSpecification extends WebPageSpecification {

    def agentService = Mock(SystemAgentService.class)

    def setup() {
        putBean(agentService)
    }

    def "Если создаём нового агента и в бд никого нет - нет ошибки валидации"() {
        setup:
        def agent = SystemAgentObjects.systemAgent()
        agent.id = null
        def validator = new ServiceLoginValidator(Model.of(agent))
        def validatable = new Validatable(StringObjects.randomString)
        agentService.isExistsAgent(_) >> false

        when:
        validator.validate(validatable)

        then:
        validatable.errors.size() == 0
    }

    def "Если создаём нового агента и в бд есть запись - ошибка валидации"() {
        setup:
        def agent = SystemAgentObjects.systemAgent()
        agent.id = null
        def validator = new ServiceLoginValidator(Model.of(agent))
        def validatable = new Validatable(StringObjects.randomString)
        agentService.isExistsAgent(_) >> true

        when:
        validator.validate(validatable)

        then:
        validatable.errors.size() == 1
    }

    def "Если редактируем запись агента, то ошибка валидации нет"() {
        setup:
        def validator = new ServiceLoginValidator(Model.of(SystemAgentObjects.systemAgent()))
        def validatable = new Validatable(StringObjects.randomString)
        agentService.isExistsAgent(_) >> true

        when:
        validator.validate(validatable)

        then:
        validatable.errors.size() == 0
    }
}
