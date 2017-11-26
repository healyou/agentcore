package dsl

import db.base.Environment
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageObjectTypeService
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgentService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.LoginService
import service.ServerTypeService
import service.objects.AgentType
import testbase.AbstractServiceTest

/**
 * Тестирование работы двух агентов, где роль сервера и вызова функций происходит в тестовом режиме
 * И того - тестим отработку всех действий при вз-вии двух агентов
 *
 * @author Nikita Gorodilov
 */
class RuntimeAgentTest extends AbstractServiceTest {

    @Autowired
    ServiceMessageService messageService
    @Autowired
    ServiceMessageObjectTypeService messageObjectTypeService
    @Autowired
    ServiceMessageTypeService messageTypeService
    @Autowired
    SystemAgentService systemAgentService
    @Autowired
    ServiceMessageService serviceMessageService
    @Autowired
    ServerTypeService serverTypeService
    @Autowired
    Environment environment
    @Autowired
    LoginService loginService

    TestRuntimeAgentClass workerAgent_a1
    TestRuntimeAgentClass serverAgent_a2

    @Before
    void setup() {
        workerAgent_a1 = new TestRuntimeAgentClass(getClass().getResource("a1_testdsl.groovy").toURI().path) {

            def senderCode = AgentType.Code.WORKER

            @Override
            protected ServerTypeService getServerTypeService() {
                return RuntimeAgentTest.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return RuntimeAgentTest.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return RuntimeAgentTest.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return RuntimeAgentTest.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return RuntimeAgentTest.this.serviceMessageService
            }
        }
        serverAgent_a2 = new TestRuntimeAgentClass(getClass().getResource("a2_testdsl.groovy").toURI().path) {

            def senderCode = AgentType.Code.SERVER

            @Override
            protected ServerTypeService getServerTypeService() {
                return RuntimeAgentTest.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return RuntimeAgentTest.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return RuntimeAgentTest.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return RuntimeAgentTest.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return RuntimeAgentTest.this.serviceMessageService
            }
        }
    }

    /* Агенты получают друг от друга сообщения и выполняют соответствующие функции */
    @Test
    void testGetAgentMessage() {
        assert workerAgent_a1 != null
        assert serverAgent_a2 != null

        workerAgent_a1.onGetMessage(createSystemSendMessage(serverAgent_a2))
        serverAgent_a2.onGetMessage(createSystemSendMessage(workerAgent_a1))

        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnGetMessageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnGetMessageFun
    }

    ServiceMessage createSystemSendMessage(TestRuntimeAgentClass agent) {
        def message = new ServiceMessage(
                "{}",
                messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE),
                messageTypeService.get(ServiceMessageType.Code.SEND),
                Collections.emptyList(),
                agent.systemAgent.id
        )
        message.senderCode = agent.senderCode

        messageService.save(message)
        message
    }
}
