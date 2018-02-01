package com.mycompany.agent.validator

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.objects.StringObjects
import com.mycompany.objects.SystemAgentObjects
import com.mycompany.service.ServerAgentService
import org.apache.wicket.model.Model
import org.apache.wicket.validation.Validatable
import spock.lang.Unroll

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

    @Unroll
    def "Валидация логина при сущ.лок.агента=#isExistsAgentLocal,агента в сервисе=#isExistsAgentService,ошибок-#errorCount" () {
        setup:
        def agent = SystemAgentObjects.systemAgent()
        agent.id = null
        def validator = new ServiceLoginValidator(Model.of(agent))
        def validatable = new Validatable(StringObjects.randomString)

        when:
        validator.validate(validatable)

        then:
        1 * systemAgentService.isExistsAgent(_) >> isExistsAgentLocal
        1 * serviceAgentService.isExistsAgent(_,_) >> isExistsAgentService
        validatable.errors.size() == errorCount

        where:
        isExistsAgentLocal | isExistsAgentService | errorCount
        false              | false                | 0
        true               | false                | 1
        false              | true                 | 1
        true               | true                 | 2
    }
}
