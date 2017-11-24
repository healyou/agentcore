package dsl;

import db.base.Environment;
import db.core.servicemessage.ServiceMessage;
import db.core.systemagent.SystemAgent;
import db.core.systemagent.SystemAgentService;
import org.jetbrains.annotations.NotNull;
import service.LoginService;
import service.ServerTypeService;
import service.SessionManager;
import service.objects.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Класс java, тк использующий его kotlin класс ничего не должен знать про groovy
 * kotlin файлы ничего не должны знать про groovy классы
 *
 * @author Nikita Gorodilov
 */
public abstract class RuntimeAgent extends ARuntimeAgent {

    private RuntimeAgentService runtimeAgentService = new RuntimeAgentService();
    private SystemAgent systemAgent = null;

    public RuntimeAgent(String path) {
        super();
        runtimeAgentService.loadExecuteRules(path);
        runtimeAgentService.applyInit();
        loadServiceTypes(runtimeAgentService);
        configureSystemAgent();
    }

    @Override
    public void onLoadImage(@Nullable Image image) {
        runtimeAgentService.applyOnLoadImage(image);
    }

    @Override
    public void onGetMessage(@NotNull ServiceMessage serviceMessage) {
        runtimeAgentService.applyOnGetMessage(serviceMessage);
    }

    @Override
    public void onEndImageTask(@Nullable Image updateImage) {
        runtimeAgentService.applyOnEndImageTask(updateImage);
    }

    @Nullable
    @Override
    protected SystemAgent getSystemAgent() {
        return systemAgent;
    }

    protected abstract ServerTypeService getServerTypeService();
    protected abstract LoginService getLoginService();
    protected abstract Environment getEnvironment();

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
                    Collections.emptyList(),
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
