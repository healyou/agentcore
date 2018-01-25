package com.mycompany.dsl

import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslImage
import objects.DslObjects
import objects.StringObjects
import objects.TypesObjects
import objects.initdbobjects.AgentObjects
import objects.initdbobjects.UserObjects
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import testbase.AbstractServiceTest
import objects.MockObjects
import objects.OtherObjects
import com.mycompany.user.User

import java.nio.file.Files

import static junit.framework.Assert.fail

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
    @Autowired
    FileContentLocator fileContentLocator
    LoginService loginService = MockObjects.loginService()

    TestRuntimeAgentClass workerAgent_a1
    TestRuntimeAgentClass serverAgent_a2

    @Before
    void setup() {
        workerAgent_a1 = new TestAgentClass(
                createDslAttachment("a1_testdsl.groovy"),
                TypesObjects.testAgent1TypeCode()
        )
        serverAgent_a2 = new TestAgentClass(
                createDslAttachment("a2_testdsl.groovy"),
                TypesObjects.testAgent2TypeCode()
        )
    }

    @Test
    void "Отправленное из dsl сообщение сохраняется в базе данных"() {
        /* отправка сообщения из onLoadImage */
        def sc = new ServiceMessageSC()
        sc.systemAgentId = workerAgent_a1.systemAgent.id
        def prevSize = serviceMessageService.get(sc).size()
        workerAgent_a1.onGetServiceMessage(new DslServiceMessage(TypesObjects.testAgentType2().code, OtherObjects.image()))
        def updateSize = serviceMessageService.get(sc).size()
        assert prevSize != updateSize && prevSize + 1 == updateSize
    }

    @Test
    void "Вызов startTask приводит к вызову onEndTask"() {
        /* отправка сообщения из onLoadImage */
        workerAgent_a1.onGetLocalMessage(new DslLocalMessage(DslObjects.getA1_testdslConditionEventName()))

        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnGetLocalMessageFun
        assert workerAgent_a1.runtimeAgentService.isExecuteTestOnGetLocalMessages
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnEndTask
    }

    @Test
    void "Выполнение всех функций агента"() {
        workerAgent_a1.onGetServiceMessage(createSystemSendMessage(serverAgent_a2))
        serverAgent_a2.onGetServiceMessage(createSystemSendMessage(workerAgent_a1))
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnGetServiceMessageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnGetServiceMessageFun

        workerAgent_a1.onGetLocalMessage(new DslLocalMessage(DslObjects.getA1_testdslConditionEventName()))
        serverAgent_a2.onGetLocalMessage(new DslLocalMessage(DslObjects.getA2_testdslConditionEventName()))
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnGetLocalMessageFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnGetLocalMessageFun

        workerAgent_a1.onEndTask(DslObjects.getA1_testdslTaskData())
        serverAgent_a2.onEndTask(DslObjects.getA2_testdslTaskData())
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnEndTask
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnEndTask

        workerAgent_a1.onEndImageTask(OtherObjects.image())
        serverAgent_a2.onEndImageTask(OtherObjects.image())
        assert workerAgent_a1.runtimeAgentService.isExecuteA1_testOnEndImageTaskFun
        assert serverAgent_a2.runtimeAgentService.isExecuteA2_testOnEndImageTaskFun
    }

    @Test
    void "Конструкторы корректно создают агента"() {
        try {
            def agent = new TestAgentClass(
                    createDslAttachment("a2_testdsl.groovy"),
                    TypesObjects.testAgent2TypeCode()
            )
            agent = new TestAgentClass(
                    AgentObjects.testAgentWithOneDslAttachment().serviceLogin,
                    TypesObjects.testAgent2TypeCode()
            )
        } catch (Exception e) {
            fail("Конструкторы должны корректно создавать агентов - " + e.message)
        }
    }

    DslServiceMessage createSystemSendMessage(TestRuntimeAgentClass agent) {
        def message = new ServiceMessage(
                "{}",
                messageTypeService.get(ServiceMessageType.Code.SEND),
                agent.systemAgent.id
        )
        message.sendAgentTypeCodes = Collections.emptyList()
        message.getMessageSenderCode = agent.senderCode
        messageService.save(message)

        new DslServiceMessage(
                message.getMessageSenderCode,
                new DslImage("testImage", [1, 2, 3] as byte[])
        )
    }

    private static DslFileAttachment createDslAttachment(String resourceFileName) {
        def path = RuntimeAgent.class.getResource(resourceFileName).toURI().path
        def file = new File(String.valueOf(path))

        OtherObjects.dslFileAttachment(resourceFileName, Files.readAllBytes(file.toPath()))
    }

    /**
     * Класс с набором сервисов
     */
    private class TestAgentClass extends TestRuntimeAgentClass {

        def senderCode

        TestAgentClass(String serviceLogin, String senderCode) {
            super(serviceLogin)
            this.senderCode = senderCode
        }

        TestAgentClass(DslFileAttachment dslFileAttachment, String senderCode) {
            super(dslFileAttachment)
            this.senderCode = senderCode
        }

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
        protected FileContentLocator getFileContentLocator() {
            return RuntimeAgentTest.this.fileContentLocator
        }

        @Override
        protected User getOwner() {
            return UserObjects.testActiveUser()
        }

        @Override
        protected User getCreateUser() {
            return UserObjects.testActiveUser()
        }
    }
}
