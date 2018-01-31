package com.mycompany.agent.validator

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.StringObjects
import com.mycompany.objects.SystemAgentObjects
import com.mycompany.service.ServerAgentService
import org.apache.wicket.model.Model
import org.apache.wicket.validation.Validatable

/**
 * @author Nikita Gorodilov
 */
class ServiceLoginValidatorSpecification extends WebPageSpecification {

    def systemAgentService = Mock(SystemAgentService.class)
    def serviceAgentService = Mock(ServerAgentService.class)

    def setup() {
        putBean(systemAgentService)
        putBean(serviceAgentService)
    }

    def "Валидация логина"() {
        setup:
        def agent = SystemAgentObjects.systemAgent()
        agent.id = null
        def validator = new ServiceLoginValidator(Model.of(agent))
        def validatable = new Validatable(StringObjects.randomString)
        systemAgentService.isExistsAgent(_) >> isExistsAgentLocal
        serviceAgentService.isExistsAgent(_,_) >> isExistsAgentService

        when:
        validator.validate(validatable)

        then:
        validatable.errors.size() == errorCount

        where:
        isExistsAgentLocal | isExistsAgentService | errorCount
        false              | false                | 0
        true               | false                | 1
        false              | true                 | 1
        true               | true                 | 2
    }

    //def "Если "
}
