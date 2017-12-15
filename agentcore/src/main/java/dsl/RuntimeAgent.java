package dsl;

import db.base.Codable;
import db.base.Environment;
import db.core.servicemessage.ServiceMessage;
import db.core.servicemessage.ServiceMessageService;
import db.core.servicemessage.ServiceMessageType;
import db.core.systemagent.SystemAgent;
import db.core.systemagent.SystemAgentService;
import dsl.base.ARuntimeAgent;
import dsl.base.SendMessageParameters;
import dsl.objects.DslImage;
import dsl.objects.DslMessage;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;
import service.AbstractAgentService;
import service.LoginService;
import service.ServerTypeService;
import service.SessionManager;
import service.objects.AgentType;
import service.objects.MessageBodyType;
import service.objects.MessageGoalType;
import service.objects.MessageType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс java, тк использующий его kotlin класс ничего не должен знать про groovy
 * а здесь используется RuntimeAgentService.groovy
 *
 * @author Nikita Gorodilov
 */
public abstract class RuntimeAgent extends ARuntimeAgent {

    private RuntimeAgentService runtimeAgentService = createRuntimeAgentService();
    private SystemAgent systemAgent = null;

    public RuntimeAgent(String path) {
        super();
        loadServiceTypes(runtimeAgentService);
        runtimeAgentService.setRuntimeAgent(this);
        runtimeAgentService.setAgentSendMessageClosure(createSendMessageClosure());
        runtimeAgentService.loadExecuteRules(path);
        runtimeAgentService.applyInit();
        configureSystemAgent();
    }

    @Override
    public void onLoadImage(@NotNull DslImage image) {
        try {
            runtimeAgentService.applyOnLoadImage(image);
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onGetMessage(@NotNull DslMessage message) {
        try {
            runtimeAgentService.applyOnGetMessage(message);
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onEndImageTask(@Nullable DslImage updateImage) {
        try {
            runtimeAgentService.applyOnEndImageTask(updateImage);
        } catch (Exception e) {
            System.out.println("Ошибка работы агента");
        }
    }

    @Nullable
    @Override
    protected SystemAgent getSystemAgent() {
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
                               String bodyFormatTypeCode) {

        if (systemAgent.getId() == null) {
            return;
        }

        String jsonObject;
        try {
            jsonObject = AbstractAgentService.Companion.toJson(image);
        } catch (Exception ignored) {
            throw new RuntimeException("Невозможно преобразовать DslImage в json");
        }

        ServiceMessageService messageService = getServiceMessageService();
        messageService.save(new ServiceMessage(
                jsonObject,
                getMessageTypeService().get(ServiceMessageType.Code.SEND),
                agentTypeCodes,
                systemAgent.getId()
        ));
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
            setTestData(runtimeAgentService); // TODO - тесты без этой штуки запустить
        }
    }

    /**
     * Установка значений по умолчанию, чтобы не включать каждый раз сервис
     */
    private void setTestData(RuntimeAgentService runtimeAgentService) {
        List<AgentType> agentTypeList = Arrays.asList(
                new AgentType(1L, "worker", "Рабочий агент", false),
                new AgentType(2L, "server", "Серверный агент", false),
                new AgentType(3L, "test_agent_type_1", "test_agent_type_1", false),
                new AgentType(4L, "test_agent_type_2", "test_agent_type_2 агент", false)
        );
        List<MessageBodyType> messageBodyTypes = Arrays.asList(
                new MessageBodyType(1L, "json", "Тело сообщения формата Json", false)
        );
        List<MessageGoalType> messageGoalTypes = Arrays.asList(
                new MessageGoalType(1L, "task_decision", "Решение задачи", false),
                new MessageGoalType(1L, "test_message_goal_type_1", "Тестовая цель общения 1", false)
        );
        List<MessageType> messageTypes = Arrays.asList(
                new MessageType(1L, "search_task_solution", "Поиск решения задачи", 1, messageGoalTypes.get(0), false),
                new MessageType(2L, "search_solution", "Поиск решения", 2, messageGoalTypes.get(0), false),
                new MessageType(3L, "solution_answer", "Ответ на запрос решения задачи", 3, messageGoalTypes.get(0), false),
                new MessageType(4L, "task_solution_answer", "Ответ на задачу", 4, messageGoalTypes.get(0), false)
        );

        runtimeAgentService.setAgentTypes(agentTypeList);
        runtimeAgentService.setMessageBodyTypes(messageBodyTypes);
        runtimeAgentService.setMessageGoalTypes(messageGoalTypes);
        runtimeAgentService.setMessageTypes(messageTypes);
    }
}
