package dsl

import db.base.Environment
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgentService
import dsl.objects.DslMessage
import dsl.objects.DslImage
import objects.TypesObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.LoginService
import service.ServerTypeService
import testbase.AbstractServiceTest
import objects.MockObjects
import objects.OtherObjects

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
    ServiceMessageTypeService messageTypeService
    @Autowired
    SystemAgentService systemAgentService
    @Autowired
    ServiceMessageService serviceMessageService
    ServerTypeService serverTypeService = MockObjects.serverTypeService()
    @Autowired
    Environment environment
    LoginService loginService = MockObjects.loginService()

    TestRuntimeAgentClass workerAgent_a1
    TestRuntimeAgentClass serverAgent_a2

    @Before
    void setup() {
        workerAgent_a1 = new TestRuntimeAgentClass(getClass().getResource("a1_testdsl.groovy").toURI().path) {

            def senderCode = TypesObjects.testAgent1TypeCode()

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

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return RuntimeAgentTest.this.messageTypeService
            }
        }
        serverAgent_a2 = new TestRuntimeAgentClass(getClass().getResource("a2_testdsl.groovy").toURI().path) {

            def senderCode = TypesObjects.testAgent2TypeCode()

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

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return RuntimeAgentTest.this.messageTypeService
            }
        }
    }

    /* Отправленное из dsl сообщение сохраняется в базе данных */
    @Test
    void testSendMessage() {
        /* отправка сообщения из onLoadImage */
        def sc = new ServiceMessageSC()
        sc.systemAgentId = workerAgent_a1.systemAgent.id
        def prevSize = serviceMessageService.get(sc).size()
        workerAgent_a1.onLoadImage(OtherObjects.image())
        def updateSize = serviceMessageService.get(sc).size()
        assert prevSize != updateSize && prevSize + 1 == updateSize
    }

    /* Выполнение функций агентами */
    @Test
    void testGetAgentMessage() {
        workerAgent_a1.onGetMessage(createSystemSendMessage(serverAgent_a2))
        serverAgent_a2.onGetMessage(createSystemSendMessage(workerAgent_a1))
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnGetMessageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnGetMessageFun

        workerAgent_a1.onLoadImage(OtherObjects.image())
        serverAgent_a2.onLoadImage(OtherObjects.image())
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnLoadImageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnLoadImageFun

        workerAgent_a1.onEndImageTask(OtherObjects.image())
        serverAgent_a2.onEndImageTask(OtherObjects.image())
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnEndImageTaskFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnEndImageTaskFun
    }

    DslMessage createSystemSendMessage(TestRuntimeAgentClass agent) {
        def message = new ServiceMessage(
                "{}",
                messageTypeService.get(ServiceMessageType.Code.SEND),
                Collections.emptyList(),
                agent.systemAgent.id
        )
        message.getMessageSenderCode = agent.senderCode
        messageService.save(message)

        new DslMessage(
                message.getMessageSenderCode,
                new DslImage("testImage", [1, 2, 3] as byte[])
        )
    }
}
