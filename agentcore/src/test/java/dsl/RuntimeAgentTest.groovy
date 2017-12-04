package dsl

import db.base.Environment
import db.core.sc.ServiceMessageSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageObjectType
import db.core.servicemessage.ServiceMessageObjectTypeService
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgentService
import dsl.objects.DslMessage
import dsl.objects.DslImage
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import service.LoginService
import service.ServerTypeService
import service.objects.AgentType
import testbase.AbstractServiceTest
import objects.MockObjects
import objects.OtherObjects

import java.awt.image.BufferedImage

import static org.easymock.EasyMock.mock

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
    ServerTypeService serverTypeService = MockObjects.serverTypeService()
    @Autowired
    Environment environment
    LoginService loginService = MockObjects.loginService()

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

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return RuntimeAgentTest.this.messageTypeService
            }

            @Override
            protected ServiceMessageObjectTypeService getMessageObjectTypeService() {
                return RuntimeAgentTest.this.messageObjectTypeService
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

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return RuntimeAgentTest.this.messageTypeService
            }

            @Override
            protected ServiceMessageObjectTypeService getMessageObjectTypeService() {
                return RuntimeAgentTest.this.messageObjectTypeService
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

        workerAgent_a1.onLoadImage(mock(DslImage.class))
        serverAgent_a2.onLoadImage(mock(DslImage.class))
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnLoadImageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnLoadImageFun

        workerAgent_a1.onEndImageTask(mock(DslImage.class))
        serverAgent_a2.onEndImageTask(mock(DslImage.class))
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnEndImageTaskFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnEndImageTaskFun
    }

    DslMessage createSystemSendMessage(TestRuntimeAgentClass agent) {
        def message = new ServiceMessage(
                "{}",
                messageObjectTypeService.get(ServiceMessageObjectType.Code.GET_SERVICE_MESSAGE),
                messageTypeService.get(ServiceMessageType.Code.SEND),
                Collections.emptyList(),
                agent.systemAgent.id
        )
        message.senderCode = agent.senderCode
        messageService.save(message)

        new DslMessage(
                message.senderCode.code,
                new DslImage("testImage", [1, 2, 3] as byte[])//AbstractAgentService.Companion.fromJson(message.jsonObject, DslImage.class)
        )
    }
}
