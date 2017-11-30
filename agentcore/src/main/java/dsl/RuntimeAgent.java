package dsl;

import db.base.Codable;
import db.base.Environment;
import db.core.servicemessage.*;
import db.core.systemagent.SystemAgent;
import db.core.systemagent.SystemAgentService;
import dsl.base.ARuntimeAgent;
import dsl.objects.DslMessage;
import dsl.objects.DslImage;
import dsl.base.SendMessageParameters;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;
import service.AbstractAgentService;
import service.LoginService;
import service.ServerTypeService;
import service.SessionManager;
import service.objects.*;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
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
        runtimeAgentService.setRuntimeAgent(this);
        runtimeAgentService.setAgentSendMessageClosure(createSendMessageClosure());
        runtimeAgentService.loadExecuteRules(path);
        runtimeAgentService.applyInit();
        loadServiceTypes(runtimeAgentService);
        configureSystemAgent();
    }

    @Override
    public void onLoadImage(@NotNull DslImage image) {
        runtimeAgentService.applyOnLoadImage(image);
    }

    @Override
    public void onGetMessage(@NotNull DslMessage message) {
        runtimeAgentService.applyOnGetMessage(message);
    }

    @Override
    public void onEndImageTask(@Nullable DslImage updateImage) {
        runtimeAgentService.applyOnEndImageTask(updateImage);
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
    protected abstract ServiceMessageTypeService getMessageTypeService();
    protected abstract ServiceMessageObjectTypeService getMessageObjectTypeService();

    /* для облегчения тестирования */
    protected RuntimeAgentService createRuntimeAgentService() {
        return new RuntimeAgentService();
    }

    protected void sendMessage(MessageType.Code messageType,
                               DslImage image,
                               List<AgentType.Code> agentTypes,
                               MessageBodyType.Code bodyFormatType,
                               MessageGoalType.Code messageGoalType) {

        if (systemAgent.getId() == null) {
            return;
        }

        /* TODO работу с изображениями надо бы переписать */
        String test;
        try {
            test = AbstractAgentService.Companion.toJson(new DslImage(
                    "testFileName",
                    new byte[] { 1 }
            ));
        } catch (Exception ignored) {
            return;
        }

        ServiceMessageService messageService = getServiceMessageService();
        messageService.save(new ServiceMessage(
                test,
                getMessageObjectTypeService().get(ServiceMessageObjectType.Code.SEND_MESSAGE_DATA),
                getMessageTypeService().get(ServiceMessageType.Code.SEND),
                agentTypes,
                systemAgent.getId()
        ));

        System.out.println("RuntimeAgent.sendMessage call");
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

                String messageType = (String) map.get(SendMessageParameters.MESSAGE_TYPE.getParamName());
                DslImage image = (DslImage) map.get(SendMessageParameters.IMAGE.getParamName());
                List<String> agentTypes =
                        map.get(SendMessageParameters.AGENT_TYPES.getParamName()) instanceof List ?
                                (List<String>) map.get(SendMessageParameters.AGENT_TYPES.getParamName()) :
                                Collections.emptyList();
                String bodyType = (String) map.get(SendMessageParameters.BODY_TYPE.getParamName());
                String messageGoalType = (String) map.get(SendMessageParameters.MESSAGE_GOAL_TYPE.getParamName());

                MessageType.Code messageTypeCode = Codable.Companion.find(MessageType.Code.class, messageType);
                List<AgentType.Code> agentTypeCodes = agentTypes.stream()
                        .map(it -> Codable.Companion.find(AgentType.Code.class, it))
                        .collect(Collectors.toList());
                MessageBodyType.Code bodyTypeCode = Codable.Companion.find(MessageBodyType.Code.class, bodyType);
                MessageGoalType.Code messageGoalTypeCode = Codable.Companion.find(MessageGoalType.Code.class, messageGoalType);

                sendMessage(messageTypeCode, image, agentTypeCodes, bodyTypeCode, messageGoalTypeCode);
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
        LoginService loginService = getLoginService();
        String password = getEnvironment().getProperty("agent.service.password");
        SessionManager sessionManager = new SessionManager();

        /* Не проверяем выход данного метода т.к. мы уже могли быть зареганы */
        // TODO заменить эти сервисы на mock в тестах - скорость работы вырастет
        loginService.registration(
                new RegistrationData(
                        runtimeAgentService.getAgentMasId().toString(),
                        runtimeAgentService.getAgentName().toString(),
                        runtimeAgentService.getAgentType().toString(),
                        password
                ),
                sessionManager
        );
        Agent agent = loginService.login(
                new LoginData(
                        runtimeAgentService.getAgentMasId().toString(),
                        password
                ),
                sessionManager
        );

        if (agent != null) {
            ServerTypeService typeService = getServerTypeService();
            List<AgentType> agentTypeList = typeService.getAgentTypes(sessionManager);
            List<MessageBodyType> messageBodyTypes = typeService.getMessageBodyTypes(sessionManager);
            List<MessageGoalType> messageGoalTypes = typeService.getMessageGoalTypes(sessionManager);
            List<MessageType> messageTypes = new ArrayList<>();
            if (messageGoalTypes != null) {
                messageGoalTypes.forEach(it -> {
                    List<MessageType> tempTypes = typeService.getMessageTypes(sessionManager, it.getCode().getCode());

                    if (tempTypes != null) {
                        messageTypes.addAll(tempTypes);
                    }
                });
            }

            runtimeAgentService.setAgentTypes(agentTypeList);
            runtimeAgentService.setMessageBodyTypes(messageBodyTypes);
            runtimeAgentService.setMessageGoalTypes(messageGoalTypes);
            runtimeAgentService.setMessageTypes(messageTypes);

        } else {
            // Тут дефолтные настройки, чтобы каждый раз не врубать сервис
            setTestData(runtimeAgentService);
        }
    }

    /**
     * Установка значений по умолчанию, чтобы не включать каждый раз сервис
     */
    private void setTestData(RuntimeAgentService runtimeAgentService) {
        List<AgentType> agentTypeList = Arrays.asList(
                new AgentType(1L, AgentType.Code.SERVER, "Рабочий агент", false),
                new AgentType(1L, AgentType.Code.WORKER, "Серверный агент", false)
        );
        List<MessageBodyType> messageBodyTypes = Arrays.asList(
                new MessageBodyType(1L, MessageBodyType.Code.JSON, "Тело сообщения формата Json", false)
        );
        List<MessageGoalType> messageGoalTypes = Arrays.asList(
                new MessageGoalType(1L, MessageGoalType.Code.TASK_DECISION, "Решение задачи", false)
        );
        List<MessageType> messageTypes = Arrays.asList(
                new MessageType(1L, MessageType.Code.SEARCH_TASK_SOLUTION, "Поиск решения задачи", 1, messageGoalTypes.get(0), false),
                new MessageType(2L, MessageType.Code.SEARCH_SOLUTION, "Поиск решения", 2, messageGoalTypes.get(0), false),
                new MessageType(3L, MessageType.Code.SOLUTION_ANSWER, "Ответ на запрос решения задачи", 3, messageGoalTypes.get(0), false),
                new MessageType(4L, MessageType.Code.TASK_SOLUTION_ANSWER, "Ответ на задачу", 4, messageGoalTypes.get(0), false)
        );

        runtimeAgentService.setAgentTypes(agentTypeList);
        runtimeAgentService.setMessageBodyTypes(messageBodyTypes);
        runtimeAgentService.setMessageGoalTypes(messageGoalTypes);
        runtimeAgentService.setMessageTypes(messageTypes);
    }
}
