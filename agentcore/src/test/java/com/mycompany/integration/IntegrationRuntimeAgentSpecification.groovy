package com.mycompany.integration

import com.mycompany.AbstractJdbcSpecification
import com.mycompany.agentworklibrary.ILibraryAgentWorkControl
import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.base.SystemEvent
import com.mycompany.dsl.exceptions.RuntimeAgentException
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import com.mycompany.dsl.loader.InstantiationTracingBeanPostProcessor
import com.mycompany.dsl.loader.RuntimeAgentWorkControl
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.service.LoginService
import com.mycompany.service.ServerAgentService
import com.mycompany.service.ServerMessageService
import com.mycompany.service.ServerTypeService
import com.mycompany.service.tasks.ServiceTask
import com.mycompany.user.User
import objects.OtherObjects
import objects.initdbobjects.UserObjects
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

import java.nio.file.Files

/**
 * @author Nikita Gorodilov
 */
@Ignore
class IntegrationRuntimeAgentSpecification extends AbstractJdbcSpecification {

    @Autowired
    ServiceMessageService messageService
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
    @Autowired
    FileContentLocator fileContentLocator
    @Autowired
    RuntimeAgentWorkControl runtimeAgentWorkControl
    ServiceTask serviceTask
    def workControl = new TestRuntimeAgentWorkControl()

    RuntimeAgent testAgent1
    RuntimeAgent testAgent2
    AgentExecuteSequence testAgentExecuteSequence
    def expectedAgentsExecuteSequence = new AgentExecuteSequence(
            /*1 агент*/
            AgentExecuteSequence.AgentExecuteFunction.ON_GET_SYSTEM_EVENT,
            AgentExecuteSequence.AgentExecuteFunction.ON_GET_LOCAL_MESSAGE,
            AgentExecuteSequence.AgentExecuteFunction.ON_END_TASK,
            /*2 агент*/
            AgentExecuteSequence.AgentExecuteFunction.ON_GET_SERVICE_MESSAGE,
            AgentExecuteSequence.AgentExecuteFunction.ON_GET_LOCAL_MESSAGE,
            /*1 агент*/
            AgentExecuteSequence.AgentExecuteFunction.ON_GET_SERVICE_MESSAGE,
    )

    def setup() {
        testAgentExecuteSequence = new AgentExecuteSequence()
        testAgent1 = new TestExecuteSequenceRuntimeAgent(createDslAttachment("integration_test_agent_1_dsl.groovy")) {

            @Override
            protected ServerTypeService getServerTypeService() {
                return IntegrationRuntimeAgentSpecification.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return IntegrationRuntimeAgentSpecification.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return IntegrationRuntimeAgentSpecification.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return IntegrationRuntimeAgentSpecification.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return IntegrationRuntimeAgentSpecification.this.messageService
            }

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return IntegrationRuntimeAgentSpecification.this.messageTypeService
            }

            @Override
            protected FileContentLocator getFileContentLocator() {
                return IntegrationRuntimeAgentSpecification.this.fileContentLocator
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
        testAgent1.withAgentExecuteSequence(testAgentExecuteSequence)
        testAgent2 = new TestExecuteSequenceRuntimeAgent(createDslAttachment("integration_test_agent_2_dsl.groovy")) {

            @Override
            protected ServerTypeService getServerTypeService() {
                return IntegrationRuntimeAgentSpecification.this.serverTypeService
            }

            @Override
            protected LoginService getLoginService() {
                return IntegrationRuntimeAgentSpecification.this.loginService
            }

            @Override
            protected Environment getEnvironment() {
                return IntegrationRuntimeAgentSpecification.this.environment
            }

            @Override
            protected SystemAgentService getSystemAgentService() {
                return IntegrationRuntimeAgentSpecification.this.systemAgentService
            }

            @Override
            protected ServiceMessageService getServiceMessageService() {
                return IntegrationRuntimeAgentSpecification.this.messageService
            }

            @Override
            protected ServiceMessageTypeService getMessageTypeService() {
                return IntegrationRuntimeAgentSpecification.this.messageTypeService
            }

            @Override
            protected FileContentLocator getFileContentLocator() {
                return IntegrationRuntimeAgentSpecification.this.fileContentLocator
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
        testAgent2.withAgentExecuteSequence(testAgentExecuteSequence)
        /**
         * Для теста сами передадим локальные сообщения
         */
        InstantiationTracingBeanPostProcessor.runtimeAgentLoader = workControl
        serviceTask = new ServiceTask(
                loginService,
                serverAgentService,
                serverMessageService,
                messageTypeService,
                messageService,
                systemAgentService,
                workControl
        )
    }

    def "Агенты успешно выполняют тестовый сценарий"() {
        when:
        executeTestScenario()

        then:
        testAgentExecuteSequence == expectedAgentsExecuteSequence
    }

    def "Агенты успешно выполняют тестовый сценарий 2 раза подряд"() {
        when:
        executeTestScenario()

        then:
        testAgentExecuteSequence == expectedAgentsExecuteSequence

        and: "Выполняем сценарий второй раз"

        when:
        testAgentExecuteSequence.clear()
        executeTestScenario()

        then:
        testAgentExecuteSequence == expectedAgentsExecuteSequence
    }

    /**
     * Тестовый сценарий взаимодействия двух агентов
     *
     * Порядок сообщений в консоли должен быть следующим:
     * 1) Начало работы агента
     * 2) Вызов функции из библиотеки функций агента testLibOnAgentStartA1
     * 3) Получение локального сообщения
     * 4) Начало выполнения задачи - функция агента testLibOnStartTaskA1
     * 5) Завершение задачи агента
     * 6) Отправка сообщения второму агенту
     * 7) Получение сообщения от первого агента
     * 8) Вызов функции из библиотеки функций агента testLibOnGetServiceMessageA2
     * 9) Получение локального сообщения
     * 10) Отправка сообщения первому агенту
     * 11) Получение сообщения от второго агента
     * 12) Завершение теста
     */
    private void executeTestScenario() {
        testAgent1.onGetSystemEvent(SystemEvent.AGENT_START)
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent2.searchMessageTimer.searchMessages()
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent1.searchMessageTimer.searchMessages()
    }

    private static DslFileAttachment createDslAttachment(String resourceFileName) {
        def path = RuntimeAgent.class.getResource(resourceFileName).toURI().path
        def file = new File(String.valueOf(path))

        OtherObjects.dslFileAttachment(resourceFileName, Files.readAllBytes(file.toPath()))
    }

    /**
     * Текущие запущенные тестовые агенты
     */
    public class TestRuntimeAgentWorkControl implements IRuntimeAgentWorkControl, ILibraryAgentWorkControl {
        @Override
        void onAgentEvent(long agentId, @NotNull String event) {
            if (agentId == testAgent1.systemAgent.id) {
                testAgent1.onGetLocalMessage(new DslLocalMessage(event))
            } else if (agentId == testAgent2.systemAgent.id) {
                testAgent2.onGetLocalMessage(new DslLocalMessage(event))
            }
        }
        @Override
        List<SystemAgent> getStartedAgents() {
            Arrays.asList(
                    testAgent1.getSystemAgent(),
                    testAgent2.getSystemAgent()
            )
        }
        @Override
        void start() {
        }
        @Override
        void stop() {
        }
        @Override
        void start(@NotNull SystemAgent agent) throws RuntimeAgentException {
        }

        @Override
        void stop(@NotNull SystemAgent agent) throws RuntimeAgentException {
        }
        @Override
        boolean isStarted(@NotNull SystemAgent agent) {
            return false
        }

        @Override
        boolean isStart(@NotNull SystemAgent agent) {
            return false
        }
    }
}
