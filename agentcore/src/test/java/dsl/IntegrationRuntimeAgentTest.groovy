package dsl

import db.base.Environment
import db.core.servicemessage.ServiceMessageObjectTypeService
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgentService
import objects.OtherObjects
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.LoginService
import service.ServerAgentService
import service.ServerMessageService
import service.ServerTypeService
import service.tasks.ServiceTask
import testbase.AbstractServiceTest

/**
 * @author Nikita Gorodilov
 */
@Ignore
class IntegrationRuntimeAgentTest extends AbstractServiceTest {

    // TODO переименовать все groovy тестовые методы таким образом, как снизу

    @Autowired
    ServiceMessageService messageService
    @Autowired
    ServiceMessageObjectTypeService messageObjectTypeService
    @Autowired
    ServiceMessageTypeService messageTypeService
    @Autowired
    SystemAgentService systemAgentService
    @Autowired
    ServerTypeService serverTypeService
    @Autowired
    Environment environment
    @Autowired
    LoginService loginService
    @Autowired
    ServerAgentService serverAgentService
    @Autowired
    ServerMessageService serverMessageService
    ServiceTask serviceTask

    RuntimeAgent testAgent1
    RuntimeAgent testAgent2

    @Before
    void setup() {
        testAgent1 = new RuntimeAgent(getClass().getResource("integration_test_agent_1_dsl.groovy").toURI().path) {

            @Override
            protected ServerTypeService getServerTypeService() {
                return IntegrationRuntimeAgentTest.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return IntegrationRuntimeAgentTest.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return IntegrationRuntimeAgentTest.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return IntegrationRuntimeAgentTest.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return IntegrationRuntimeAgentTest.this.messageService
            }

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return IntegrationRuntimeAgentTest.this.messageTypeService
            }

            @Override
            protected ServiceMessageObjectTypeService getMessageObjectTypeService() {
                return IntegrationRuntimeAgentTest.this.messageObjectTypeService
            }
        }
        testAgent2 = new RuntimeAgent(getClass().getResource("integration_test_agent_2_dsl.groovy").toURI().path) {

            @Override
            protected ServerTypeService getServerTypeService() {
                return IntegrationRuntimeAgentTest.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return IntegrationRuntimeAgentTest.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return IntegrationRuntimeAgentTest.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return IntegrationRuntimeAgentTest.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return IntegrationRuntimeAgentTest.this.messageService
            }

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return IntegrationRuntimeAgentTest.this.messageTypeService
            }

            @Override
            protected ServiceMessageObjectTypeService getMessageObjectTypeService() {
                return IntegrationRuntimeAgentTest.this.messageObjectTypeService
            }
        }
        serviceTask = new ServiceTask(
                loginService,
                serverAgentService,
                serverMessageService,
                messageObjectTypeService,
                messageTypeService,
                messageService,
                systemAgentService
        )
    }

    @Test
    void "Сценарий взаимодействия двух агентов с сервисом"() {
        testAgent1.onLoadImage(OtherObjects.image())
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent2.searchMessages()

        println("ggwp")
    }
}
