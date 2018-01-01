package integration

import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.RuntimeAgent
import objects.OtherObjects
import objects.initdbobjects.UserObjects
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import com.mycompany.service.LoginService
import com.mycompany.service.ServerAgentService
import com.mycompany.service.ServerMessageService
import com.mycompany.service.ServerTypeService
import com.mycompany.service.tasks.ServiceTask
import testbase.AbstractServiceTest
import com.mycompany.user.User

import java.nio.file.Files

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
    @Autowired
    FileContentLocator fileContentLocator
    ServiceTask serviceTask

    RuntimeAgent testAgent1
    RuntimeAgent testAgent2

    @Before
    void setup() {
        testAgent1 = new RuntimeAgent(createDslAttachment("integration_test_agent_1_dsl.groovy")) {

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
            protected FileContentLocator getFileContentLocator() {
                return IntegrationRuntimeAgentTest.this.fileContentLocator
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
        testAgent2 = new RuntimeAgent(createDslAttachment("integration_test_agent_2_dsl.groovy")) {

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
            protected FileContentLocator getFileContentLocator() {
                return IntegrationRuntimeAgentTest.this.fileContentLocator
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
        serviceTask = new ServiceTask(
                loginService,
                serverAgentService,
                serverMessageService,
                messageTypeService,
                messageService,
                systemAgentService
        )
    }

    @Test
    void "Сценарий взаимодействия двух агентов с сервисом"() {
        testScenario()
    }

    @Test
    void "Повторение сценария взаимодействия двух агентов с сервисом 2 раза подряд"() {
        testScenario()
        testScenario()
    }

    /**
     * Тестовый сценарий взаимодействия двух агентов
     *
     * Порядок сообщений в консоли должен быть следующим:
     * 1) Работа над загруженным изображением первым тестовым агентов
     * 2) Работы над изображением закончена. Отправка сообщения второму тестовому агенту первым тестовым агентов
     * 3) Получение сообщения с сервиса от первого тестового агента вторым тестовым агентом. Работа над изображением
     * 4) Работы над изображением закончена. Отправка сообщения первому тестовому агенту вторым тестовым агентов
     * 5) Получение сообщения с сервиса от второго тестового агента первым тестовым агентом. Конец работы
     */
    private void testScenario() {
        testAgent1.onLoadImage(OtherObjects.image())
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent2.searchMessages()
        serviceTask.sendMessages()
        serviceTask.getMessages()
        testAgent1.searchMessages()
    }

    private static DslFileAttachment createDslAttachment(String resourceFileName) {
        def path = RuntimeAgent.class.getResource(resourceFileName).toURI().path
        def file = new File(String.valueOf(path))

        OtherObjects.dslFileAttachment(resourceFileName, Files.readAllBytes(file.toPath()))
    }
}
