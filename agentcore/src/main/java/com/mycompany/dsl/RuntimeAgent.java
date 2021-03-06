package com.mycompany.dsl;

import com.mycompany.db.base.Environment;
import com.mycompany.db.core.file.FileContentLocator;
import com.mycompany.db.core.file.dslfile.DslFileAttachment;
import com.mycompany.db.core.servicemessage.ServiceMessage;
import com.mycompany.db.core.servicemessage.ServiceMessageService;
import com.mycompany.db.core.servicemessage.ServiceMessageType;
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService;
import com.mycompany.db.core.systemagent.SystemAgent;
import com.mycompany.db.core.systemagent.SystemAgentService;
import com.mycompany.dsl.base.IRuntimeAgent;
import com.mycompany.dsl.base.SystemEvent;
import com.mycompany.dsl.base.behavior.ARuntimeAgentBehavior;
import com.mycompany.dsl.base.behavior.IRuntimeAgentBehaviorEventSink;
import com.mycompany.dsl.base.parameters.SendServiceMessageParameters;
import com.mycompany.dsl.exceptions.RuntimeAgentException;
import com.mycompany.dsl.objects.DslAgentData;
import com.mycompany.dsl.objects.DslLocalMessage;
import com.mycompany.dsl.objects.DslServiceMessage;
import com.mycompany.dsl.objects.DslTaskData;
import com.mycompany.service.LoginService;
import com.mycompany.service.ServerTypeService;
import com.mycompany.service.SessionManager;
import com.mycompany.service.objects.*;
import com.mycompany.user.User;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Класс агента, выполняющего работу над изображением
 * Конфигурируется с помощью dsl на groovy
 *
 * Класс java, тк использующий его kotlin класс ничего не должен знать про groovy
 * а здесь используется RuntimeAgentService.groovy
 *
 * @author Nikita Gorodilov
 */
public abstract class RuntimeAgent implements IRuntimeAgent {

    private RuntimeAgentService runtimeAgentService = createRuntimeAgentService();
    private SystemAgent systemAgent = null;
    private List<IRuntimeAgentBehaviorEventSink> behaviors = new ArrayList<>();

    /* Видимость оставлена для тестирования */
    protected AgentSearchMessageTimer searchMessageTimer;
    private boolean isStart = false;

    /**
     * Конструктор без создания dsl файла агента
     *      Агент должен быть создан
     *      Dsl файл будет взят из бд
     *
     * @param serviceLogin идентификатор агента относительно многоагентной системы(логин для входа в сервис)
     */
    public RuntimeAgent(String serviceLogin) throws RuntimeAgentException {
        super();
        DslFileAttachment dslFileAttachment = loadDslFileAttachment(serviceLogin);
        initAgentServiceWithSystemAgent(dslFileAttachment);
        initSearchMessageTimer();
    }

    /**
     * Конструктор с созданием нового dsl файла агента
     *      Агент может быть ещё не создан
     *      Новый dsl(isNew) файл будет записан в бд и станет текущий для агента
     *
     * @param dslFileAttachment dsl файл агента
     */
    public RuntimeAgent(DslFileAttachment dslFileAttachment) throws RuntimeAgentException {
        super();
        initAgentServiceWithSystemAgent(dslFileAttachment);
        initSearchMessageTimer();
    }

    private void initAgentServiceWithSystemAgent(DslFileAttachment dslFileAttachment) throws RuntimeAgentException {
        loadServiceTypes(runtimeAgentService);
        runtimeAgentService.setAgentSendMessageClosure(createSendMessageClosure());
        runtimeAgentService.setAgentOnEndTaskClosure(createOnEndTaskClosure());
        runtimeAgentService.setConfigureAgentDataClosure(createConfigureAgentDataClosure());
        runtimeAgentService.loadExecuteRules(getRules(dslFileAttachment));
        runtimeAgentService.applyInit();
        systemAgent = configureAgentWithError(dslFileAttachment);
    }

    private void initSearchMessageTimer() {
        searchMessageTimer = new AgentSearchMessageTimer(
                getServiceMessageService(), getMessageTypeService(), getSystemAgentId(), this::onGetServiceMessage);
    }

    @Override
    public void start() {
        searchMessageTimer.start();
        behaviors.forEach(IRuntimeAgentBehaviorEventSink::onStart);
        onGetSystemEvent(SystemEvent.AGENT_START);
        isStart = true;
    }

    protected abstract SystemAgentService getSystemAgentService();
    protected abstract ServiceMessageService getServiceMessageService();
    protected abstract ServiceMessageTypeService getMessageTypeService();

    @Override
    public void stop() {
        searchMessageTimer.stop();
        behaviors.forEach(IRuntimeAgentBehaviorEventSink::onStop);
        onGetSystemEvent(SystemEvent.AGENT_STOP);
        isStart = false;
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    @Override
    public void onGetServiceMessage(@NotNull DslServiceMessage serviceMessage) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnGetServiceMessage(serviceMessage);
            });
            runtimeAgentService.applyOnGetServiceMessage(serviceMessage);
            behaviors.forEach(it -> {
                it.afterOnGetServiceMessage(serviceMessage);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onGetLocalMessage(@NotNull DslLocalMessage localMessage) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnGetLocalMessage(localMessage);
            });
            runtimeAgentService.applyOnGetLocalMessage(localMessage);
            behaviors.forEach(it -> {
                it.afterOnGetLocalMessage(localMessage);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onEndTask(@NotNull DslTaskData taskData) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnEndTask(taskData);
            });
            runtimeAgentService.applyOnEndTask(taskData);
            behaviors.forEach(it -> {
                it.afterOnEndTask(taskData);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void onGetSystemEvent(@NotNull SystemEvent systemEvent) {
        try {
            behaviors.forEach(it -> {
                it.beforeOnGetSystemEvent(systemEvent);
            });
            runtimeAgentService.applyOnGetSystemEvent(systemEvent);
            behaviors.forEach(it -> {
                it.afterOnGetSystemEvent(systemEvent);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка работы агента");
        }
    }

    @Override
    public void add(@NotNull IRuntimeAgentBehaviorEventSink behavior) {
        behaviors.add(behavior);
        behavior.bind(this);
    }

    @Override
    public void delete(@NotNull IRuntimeAgentBehaviorEventSink behavior) {
        if (behaviors.remove(behavior)) {
            behavior.unbind();
        }
    }

    @NotNull
    @Override
    public SystemAgent getSystemAgent() {
        return systemAgent;
    }

    private Long getSystemAgentId() {
        Long id = systemAgent.getId();
        if (id == null) {
            throw new RuntimeAgentException("id Системного агента не может быть null");
        }
        return id;
    }

    public RuntimeAgentService getRuntimeAgentService() {
        return runtimeAgentService;
    }

    protected abstract ServerTypeService getServerTypeService();
    protected abstract LoginService getLoginService();
    protected abstract Environment getEnvironment();
    protected abstract FileContentLocator getFileContentLocator();
    protected abstract User getOwner();
    protected abstract User getCreateUser();

    /* для облегчения тестирования */
    protected RuntimeAgentService createRuntimeAgentService() {
        return new RuntimeAgentService();
    }

    protected void sendServiceMessage(String messageTypeCode,
                                      String messageBody,
                                      List<String> agentTypeCodes,
                                      String messageBodyTypeCode) {
        if (systemAgent.getId() == null) {
            throw new RuntimeAgentException("Агент не инициализирован");
        }

        ServiceMessageService messageService = getServiceMessageService();
        ServiceMessage serviceMessage = new ServiceMessage(
                messageBody,
                getMessageTypeService().get(ServiceMessageType.Code.SEND),
                systemAgent.getId()
        );
        serviceMessage.setSendAgentTypeCodes(agentTypeCodes);
        serviceMessage.setSendMessageType(messageTypeCode);
        serviceMessage.setSendMessageBodyType(messageBodyTypeCode);
        messageService.save(serviceMessage);
    }

    /**
     * Получение dsl файла агента, агент должен уже существовать
     */
    private DslFileAttachment loadDslFileAttachment(String agentServiceLogin) {
        SystemAgentService agentService = getSystemAgentService();
        if (!agentService.isExistsAgent(agentServiceLogin)) {
            throw new RuntimeAgentException("Нельзя создать агента ");
        }
        return agentService.getDslAttachment(agentServiceLogin);
    }

    private String getRules(DslFileAttachment dslFileAttachment) {
        try {
            return new String(dslFileAttachment.contentAsByteArray(getFileContentLocator()), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeAgentException("Ошибка преобразования byte[] данных dsl файла в строку");
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

                String messageTypeCode = (String) map.get(SendServiceMessageParameters.MESSAGE_TYPE.getParamName());
                String messageBody = (String) map.get(SendServiceMessageParameters.MESSAGE_BODY.getParamName());
                List<String> agentTypeCodes =
                        map.get(SendServiceMessageParameters.AGENT_TYPES.getParamName()) instanceof List ?
                                (List<String>) map.get(SendServiceMessageParameters.AGENT_TYPES.getParamName()) :
                                Collections.emptyList();
                String bodyTypeCode = (String) map.get(SendServiceMessageParameters.BODY_TYPE.getParamName());

                sendServiceMessage(messageTypeCode, messageBody, agentTypeCodes, bodyTypeCode);
                return null;
            }
        };
    }

    /**
     * Функция вызывается из groovy
     * Выполняется при завершении работы агентом
     */
    private Closure<Void> createOnEndTaskClosure() {
        return new Closure<Void>(null) {
            @Override
            public Void call(Object argument) {
                if (!(argument instanceof String)) return null;

                DslTaskData taskData = new DslTaskData((String) argument);

                onEndTask(taskData);
                return null;
            }
        };
    }

    /**
     * Функция вызывается из groovy
     * Выполняется при завершении работы агентом
     */
    private Closure<DslAgentData> createConfigureAgentDataClosure() {
        return new Closure<DslAgentData>(null) {
            @Override
            public DslAgentData call() {
                return new DslAgentData(getSystemAgentId());
            }
        };
    }

    private SystemAgent configureAgentWithError(DslFileAttachment dslFileAttachment) {
        if (isSuccessLogin()) {
            return configureSystemAgent(dslFileAttachment);
        } else {
            throw new RuntimeAgentException("Невозможно зарегистрировать агента в сервисе");
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
    private SystemAgent configureSystemAgent(DslFileAttachment dslFileAttachment) {
        SystemAgentService agentService = getSystemAgentService();
        String agentMasId = String.valueOf(runtimeAgentService.getAgentMasId());
        if (!agentService.isExistsAgent(agentMasId)) {
            SystemAgent systemAgent = new SystemAgent(
                    agentMasId,
                    getEnvironment().getProperty("agent.service.password"),
                    true,
                    getOwner(),
                    getCreateUser()
            );
            systemAgent.setDslFile(dslFileAttachment);
            agentService.save(systemAgent);
        } else {
            /* Обновляем dsl файл */
            systemAgent = agentService.getByServiceLogin(agentMasId);
            systemAgent.setDslFile(dslFileAttachment);
            agentService.save(systemAgent);
        }
        return agentService.getByServiceLogin(agentMasId);
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
        runtimeAgentService.setServiceMessageTypes(messageTypes);

        if (agentTypeList == null || messageBodyTypes == null || messageGoalTypes == null || messageTypes == null) {
            throw new RuntimeAgentException("Сервис с типами данных недоступен");
        }
    }

    @FunctionalInterface
    public interface OnGetServiceMessageFunction {
        void onGetServiceMessage(@NotNull DslServiceMessage serviceMessage);
    }
}
