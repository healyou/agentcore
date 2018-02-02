package com.mycompany

import objects.initdbobjects.AgentObjects

/**
 * @author Nikita Gorodilov
 */
class TestDb extends AbstractJdbcSpecification {

    def "тест"() {
        when:
        def k = AgentObjects.testAgentWithManyDslAttachment()
        def k1 = 1

        then:
        true
    }
}
