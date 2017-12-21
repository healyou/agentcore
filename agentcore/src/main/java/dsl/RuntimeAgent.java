package dsl;

import db.base.Environment;
import db.core.servicemessage.ServiceMessage;
import db.core.servicemessage.ServiceMessageService;
import db.core.servicemessage.ServiceMessageType;
import db.core.systemagent.SystemAgent;
import db.core.systemagent.SystemAgentService;
import dsl.base.ARuntimeAgent;
import dsl.base.SendMessageParameters;
import dsl.base.behavior.ARuntimeAgentBehavior;
import dsl.objects.DslImage;
import dsl.objects.DslMessage;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;
import service.AbstractAgentService;
import service.LoginService;
import service.ServerTypeService;
import service.SessionManager;
import service.objects.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Класс java, тк использующий его kotlin класс ничего не должен знать про groovy
 * а здесь используется RuntimeAgentService.groovy
 *
 * @author Nikita Gorodilov
 */
public abstract class RuntimeAgent extends ARuntimeAgent {

    private RuntimeAgentService runtimeAgentService = createRuntimeAgentService();
    private SystemAgent systemAgent = null;
    private List<ARuntimeAgentBehavior> behaviors = new ArrayList<>();

    public RuntimeAgent(String path) {
        super();
        loadServiceTypes(runtimeAgentService);
        runtimeAgentService.setRuntimeAgent(this);
        runtimeAgentService.setAgentSendMessageClosure(createSendMessageClosure());
        runtimeAgentService.loadExecuteRules(path);
        runtimeAgentService.applyInit();
        configureAgentWithError();
    }

    @Override
    public void start() {
        super.start();
        behaviors.forEach(ARuntimeAgentBehavior::onStart);
    }

    @Override
    public void stop() {
        super.stop();
        behaviors.forEach(ARuntimeAgentBehavior::onStop);
    }

    @Override
    public void onLoadImage(@NotNull DslImage image) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnLoadImage(image);
            });
            runtimeAgentService.applyOnLoadImage(image);
            behaviors.forEach(it -> {
                it.afterOnLoadImage(image);
            });
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onGetMessage(@NotNull DslMessage message) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnGetMessage(message);
            });
            runtimeAgentService.applyOnGetMessage(message);
            behaviors.forEach(it -> {
                it.afterOnGetMessage(message);
            });
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onEndImageTask(@NotNull DslImage updateImage) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnEndImageTask(updateImage);
            });
            runtimeAgentService.applyOnEndImageTask(updateImage);
            behaviors.forEach(it -> {
                it.afterOnEndImageTask(updateImage);
            });
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    public RuntimeAgent add(ARuntimeAgentBehavior behavior) {
        behaviors.add(behavior);
        behavior.bing();
        return this;
    }

    public RuntimeAgent remove(ARuntimeAgentBehavior behavior)  {
        if (behaviors.remove(behavior)) {
            behavior.unbind();
        }
        return this;
    }

    @Nullable
    @Override
    public SystemAgent getSystemAgent() {
        return systemAgent;
    }

    public RuntimeAgentService getRuntimeAgentService() {
        return runtimeAgentService;
    }

    protected abstract ServerTypeService getServerTypeService();
    protected abstract LoginService getLoginService();
    protected abstract Environment getEnvironment();

    /* для облегчения тестирования */
    protected RuntimeAgentService createRuntimeAgentService() {
        return new RuntimeAgentService();
    }

    protected void sendMessage(String messageTypeCode,
                               DslImage image,
                               List<String> agentTypeCodes,
                               String messageBodyTypeCode) {
        if (systemAgent.getId() == null) {
            return;
        }
        String jsonObject = imageToJsonWithThrowError(image);

        ServiceMessageService messageService = getServiceMessageService();
        ServiceMessage serviceMessage = new ServiceMessage(
                jsonObject,
                getMessageTypeService().get(ServiceMessageType.Code.SEND),
                systemAgent.getId()
        );
        serviceMessage.setSendAgentTypeCodes(agentTypeCodes);
        serviceMessage.setSendMessageType(messageTypeCode);
        serviceMessage.setSendMessageBodyType(messageBodyTypeCode);
        messageService.save(serviceMessage);
    }

    private String imageToJsonWithThrowError(DslImage image) {
        try {
            return AbstractAgentService.Companion.toJson(image);
        } catch (Exception ignored) {
            throw new RuntimeException("Невозможно преобразовать DslImage в json");
        }
    }

    /**
     * Функция вызывается из groovy
     * Идёт обработка всех параметров и передача управления самим функциям отправки сообщения
     */
    private Closure<Void> createSendMessageClosure() {
        return new Closure<Void>(null) {

            @Override
            public Void call(Object arguments) {
                /* Проверки и дефолтные поля выставляются в dsl */
                if (!(arguments instanceof Map)) return null;
                Map map = (Map) arguments;

                String messageTypeCode = (String) map.get(SendMessageParameters.MESSAGE_TYPE.getParamName());
                DslImage image = (DslImage) map.get(SendMessageParameters.IMAGE.getParamName());
                List<String> agentTypeCodes =
                        map.get(SendMessageParameters.AGENT_TYPES.getParamName()) instanceof List ?
                                (List<String>) map.get(SendMessageParameters.AGENT_TYPES.getParamName()) :
                                Collections.emptyList();
                String bodyTypeCode = (String) map.get(SendMessageParameters.BODY_TYPE.getParamName());

                sendMessage(messageTypeCode, image, agentTypeCodes, bodyTypeCode);
                return null;
            }
        };
    }

    private void configureAgentWithError() {
        if (isSuccessLogin()) {
            configureSystemAgent();
        } else {
            throw new RuntimeException("Невозможно зарегистрировать агента в сервисе");
        }
    }

    /**
     * Регистрация агента в сервисе
     *
     * @return true - агент может выполнять вход на сервис
     */
    private boolean isSuccessLogin() {
        String masId = String.valueOf(runtimeAgentService.getAgentMasId());
        String password = getEnvironment().getProperty("agent.service.password");

        SessionManager sessionManager = new SessionManager();
        getLoginService().registration(
                new RegistrationData(
                        masId,
                        String.valueOf(runtimeAgentService.getAgentName()),
                        String.valueOf(runtimeAgentService.getAgentType()),
                        password
                ),
                sessionManager
        );

        boolean isSuccessLogin = getLoginService().login(new LoginData(masId, password), sessionManager) != null;
        if (isSuccessLogin) {
            getLoginService().logout(sessionManager);
        }

        return isSuccessLogin;
    }

    /**
     * Создание и получение системного агента(из локальной бд)
     */
    private void configureSystemAgent() {
        String agentMasId = String.valueOf(runtimeAgentService.getAgentMasId());
        SystemAgentService systemAgentService = getSystemAgentService();
        if (!systemAgentService.isExistsAgent(agentMasId)) {
            systemAgentService.create(new SystemAgent(
                    agentMasId,
                    getEnvironment().getProperty("agent.service.password"),
                    true
            ));
        }
        systemAgent = getSystemAgentService().getByServiceLogin(agentMasId);
    }

    private void loadServiceTypes(RuntimeAgentService runtimeAgentService) {
        SessionManager sessionManager = new SessionManager();
        ServerTypeService typeService = getServerTypeService();
        List<AgentType> agentTypeList = typeService.getAgentTypes(sessionManager);
        List<MessageBodyType> messageBodyTypes = typeService.getMessageBodyTypes(sessionManager);
        List<MessageGoalType> messageGoalTypes = typeService.getMessageGoalTypes(sessionManager);
        List<MessageType> messageTypes = typeService.getMessageTypes(sessionManager);

        runtimeAgentService.setAgentTypes(agentTypeList);
        runtimeAgentService.setMessageBodyTypes(messageBodyTypes);
        runtimeAgentService.setMessageGoalTypes(messageGoalTypes);
        runtimeAgentService.setMessageTypes(messageTypes);

        if (agentTypeList == null || messageBodyTypes == null || messageGoalTypes == null || messageTypes == null) {
            // Тут дефолтные настройки, чтобы каждый раз не врубать сервис
            System.out.println("Загрузка дефолтных параметров агента(сервис недоступен типов данных там нет)");
            //setTestData(runtimeAgentService); // тесты работают и без этой строчки
            throw new RuntimeException("Сервис с типами данных недоступен");
        }
    }

    /**
     * Установка значений по умолчанию, чтобы не включать каждый раз сервис
     */
    private void setTestData(RuntimeAgentService runtimeAgentService) {
        List<AgentType> agentTypeList = Arrays.asList(
                new AgentType(1L, "manual_test_agent_1_masId", "Тестовый агент 1(Ручное тестировние)", false),
                new AgentType(2L, "manual_test_agent_2_masId", "Тестовый агент 2(Ручное тестировние)", false)
        );
        List<MessageBodyType> messageBodyTypes = Collections.singletonList(
                new MessageBodyType(1L, "json", "Тело сообщения формата Json", false)
        );
        List<MessageGoalType> messageGoalTypes = Collections.singletonList(
                new MessageGoalType(1L, "manual_test_message_goal_type_1",
                        "Тестовая цель общения 1(Ручное тестировние)", false)
        );
        List<MessageType> messageTypes = Arrays.asList(
                new MessageType(1L, "manual_test_message_type_1_test_goal_2",
                        "Тестовый тип сообщения 1 для тестовой цели 2(Ручное тестировние)", 1,
                        messageGoalTypes.get(0), false),
                new MessageType(2L, "manual_test_message_type_2_test_goal_2",
                        "Тестовый тип сообщения 2 для тестовой цели 2(Ручное тестировние)", 2,
                        messageGoalTypes.get(0), false),
                new MessageType(3L, "solution_answer", "Ответ на запрос решения задачи", 3,
                        messageGoalTypes.get(0), false),
                new MessageType(4L, "task_solution_answer", "Ответ на задачу", 4,
                        messageGoalTypes.get(0), false)
        );

        runtimeAgentService.setAgentTypes(agentTypeList);
        runtimeAgentService.setMessageBodyTypes(messageBodyTypes);
        runtimeAgentService.setMessageGoalTypes(messageGoalTypes);
        runtimeAgentService.setMessageTypes(messageTypes);
    }
}
