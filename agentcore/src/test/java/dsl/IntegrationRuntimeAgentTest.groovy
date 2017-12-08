package dsl

import db.base.Environment
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
 * Тестирование взаимодействие двух агентов с сервисом
 * - сервис должен быть включен
 * - не используются тестовые классы, только то, что будет использовано при работе прилоежния
 *
 * @author Nikita Gorodilov
 */
@Ignore
class IntegrationRuntimeAgentTest extends AbstractServiceTest {

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
        }
        serviceTask = new ServiceTask(
                loginService,
                serverAgentService,
                serverMessageService,
                messageTypeService,
                messageService,
                systemAgentService
        )
    }

    /**
     * Порядок сообщений в консоли должен быть следующим:
     * 1) Работа над загруженным изображением первым тестовым агентов
     * 2) Работы над изображением закончена. Отправка сообщения второму тестовому агенту первым тестовым агентов
     * 3) Получение сообщения с сервиса от первого тестового агента вторым тестовым агентом. Работа над изображением
     * 4) Работы над изображением закончена. Отправка сообщения первому тестовому агенту вторым тестовым агентов
     * 5) Получение сообщения с сервиса от второго тестового агента первым тестовым агентом. Конец работы
     */
    @Test
    void "Сценарий взаимодействия двух агентов с сервисом"() {
        testAgent1.onLoadImage(OtherObjects.image())
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent2.searchMessages()
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent1.searchMessages()
    }
}
