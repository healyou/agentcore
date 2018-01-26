package com.mycompany.dsl

import com.mycompany.agentworklibrary.AAgentWorkLibrary
import com.mycompany.dsl.base.SystemEvent
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.base.SendServiceMessageParameters
import com.mycompany.dsl.objects.DslTaskData
import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.MessageBodyType
import com.mycompany.service.objects.MessageGoalType
import com.mycompany.service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentService {

    def agentType = null
    def agentName = null
    def agentMasId = null
    def defaultBodyType = null

    /* Типы данных сервиса обмена сообщениями */
    List<AgentType> agentTypes = []
    List<MessageBodyType> messageBodyTypes = []
    List<MessageGoalType> messageGoalTypes = []
    List<MessageType> serviceMessageTypes = []
    /* Типы данных блока инициализации */
    List<String> localMessageTypes = []
    List<String> taskTypes = []

    boolean on_get_service_message_provided = false
    def onGetServiceMessage = {}
    boolean on_get_local_message_provided = false
    def onGetLocalMessage = {}
    boolean on_end_task_provided = false
    def onEndTask = {}
    boolean on_get_system_event_provided = false
    def onGetSystemEvent = {}
    boolean init_provided = false
    def init = {}
    boolean agent_send_message_provided = false
    def agentSendServiceMessage = {}
    boolean agent_on_end_task_provided = false
    def agentOnEndTask = {}

    /**
     * Класс, который содержит функции для работы агента
     */
    def agentWorkLibraryClass = AAgentWorkLibrary.findAgentWorkLibraryClass()

    void loadExecuteRules(String rules) {
        Binding binding = createLoadBindings()

        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(rules)

        checkLoadRules(binding)
    }

    Binding createLoadBindings() {
        Binding binding = new Binding()

        binding.init = init
        binding.onGetServiceMessage = onGetServiceMessage
        binding.onGetLocalMessage = onGetLocalMessage
        binding.onEndTask = onEndTask
        binding.onGetSystemEvent = onGetSystemEvent

        return binding
    }

    void setAgentSendMessageClosure(Closure c) {
        agent_send_message_provided = true
        agentSendServiceMessage = c
    }

    void setAgentOnEndTaskClosure(Closure c) {
        agent_on_end_task_provided = true
        agentOnEndTask = c
    }

    void checkLoadRules(Binding binding) {
        if (bindingFunctionCheck(binding)) {
            init = binding.init
            onGetServiceMessage = binding.onGetServiceMessage
            onGetLocalMessage = binding.onGetLocalMessage
            onEndTask = binding.onEndTask
            onGetSystemEvent = binding.onGetSystemEvent

            on_get_service_message_provided = true
            on_get_local_message_provided = true
            on_end_task_provided = true
            on_get_system_event_provided = true
            init_provided = true
        } else {
            throw new RuntimeException("Неправильная dsl")
        }
    }

    /* Проверка функций */
    boolean bindingFunctionCheck(Binding binding) {
        return binding.init != init && binding.onGetServiceMessage != onGetServiceMessage &&
                binding.onGetLocalMessage != onGetLocalMessage && binding.onEndTask != onEndTask &&
                binding.onGetSystemEvent != onGetSystemEvent
    }

    void applyInit() {
        if (init_provided) {
            Binding binding = new Binding()
            prepareTypes(binding)
            prepareInitData(binding)
            binding.init = init

            /**
             * GroovyShell изначально delegate owner = this class
             * а надо script, потому что только через binding можно сохранить изменения в evaluate
             */
            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("init.delegate = this;init.resolveStrategy = Closure.DELEGATE_FIRST;init()")

            try {
                agentType = binding.type
                agentName = binding.name
                agentMasId = binding.masId
                defaultBodyType = binding.defaultBodyType
                localMessageTypes = binding.localMessageTypes
                taskTypes = binding.taskTypes
            } catch (ignored) {
                throw new RuntimeException("Нет данных для инициализации агента")
            }
            if (initDataIsNullOrEmpty()) {
                throw new RuntimeException("Нет данных для инициализации агента")
            }
        } else {
            throw new RuntimeException("Функция init не загружена")
        }
    }

    void applyOnGetServiceMessage(DslServiceMessage serviceMessage) {
        if (on_get_service_message_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.serviceMessage = serviceMessage

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onGetServiceMessage.delegate = this;onGetServiceMessage.resolveStrategy = Closure.DELEGATE_FIRST;onGetServiceMessage(serviceMessage)")
        } else {
            throw new RuntimeException("Функция on_get_service_message не загружена")
        }
    }

    void applyOnGetLocalMessage(DslLocalMessage localMessage) {
        if (on_get_local_message_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.localMessage = localMessage

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onGetLocalMessage.delegate = this;onGetLocalMessage.resolveStrategy = Closure.DELEGATE_FIRST;onGetLocalMessage(localMessage)")
        } else {
            throw new RuntimeException("Функция on_get_local_message не загружена")
        }
    }

    void applyOnEndTask(DslTaskData taskData) {
        if (on_end_task_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.taskData = taskData

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onEndTask.delegate = this;onEndTask.resolveStrategy = Closure.DELEGATE_FIRST;onEndTask(taskData)")
        } else {
            throw new RuntimeException("Функция on_end_task не загружена")
        }
    }

    void applyOnGetSystemEvent(SystemEvent systemEvent) {
        if (on_end_task_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.systemEvent = systemEvent

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onGetSystemEvent.delegate = this;onGetSystemEvent.resolveStrategy = Closure.DELEGATE_FIRST;onGetSystemEvent(systemEvent)")
        } else {
            throw new RuntimeException("Функция on_end_task не загружена")
        }
    }

    void prepareClosures(Binding binding) {
        binding.init = init
        binding.onGetServiceMessage = onGetServiceMessage
        binding.onGetLocalMessage = onGetLocalMessage
        binding.onEndTask = onEndTask
        binding.onGetSystemEvent = onGetSystemEvent
        binding.sendServiceMessage = { Map map ->
            /* required fields */
            SendServiceMessageParameters.values().each {
                if (it.required && map[it.paramName] == null) {
                    throw new RuntimeException("Обязательный параметр '" + it.paramName + "' не найден")
                }
            }

            /* default value fields */
            def bodyTypeParamName = SendServiceMessageParameters.BODY_TYPE.paramName
            def bodyType = map[bodyTypeParamName]
            if (bodyType == null) {
                map[bodyTypeParamName] = defaultBodyType
            }

            if (agent_send_message_provided) {
                agentSendServiceMessage.call(map)

            } else {
                throw new RuntimeException("Функция sendServiceMessage не загружена")
            }
        }
        binding.executeCondition = { spec, closure ->
            closure.delegate = delegate
            binding.result = true
            binding.and = true
            closure()
        }
        binding.condition = { closure ->
            closure.delegate = delegate
            if (binding.and)
                binding.result = (closure() && binding.result)
            else
                binding.result = (closure() || binding.result)
        }
        binding.allOf = { closure ->
            closure.delegate = delegate
            def storeResult = binding.result
            def storeAnd = binding.and

            binding.result = true
            binding.and = true
            closure()

            if (storeAnd) {
                binding.result = (storeResult && binding.result)
            } else {
                binding.result = (storeResult || binding.result)
            }
            binding.and = storeAnd
        }
        binding.anyOf = { closure ->
            closure.delegate = delegate
            def storeResult = binding.result
            def storeAnd = binding.and

            binding.result = false // Starting premise is false
            binding.and = false
            closure()
            if (storeAnd) {
                binding.result = (storeResult && binding.result)
            } else {
                binding.result = (storeResult || binding.result)
            }
            binding.and = storeAnd
        }
        binding.execute = { closure ->
            closure.delegate = delegate

            if (binding.result) {
                binding.isStartTask = true
                use(agentWorkLibraryClass) {
                    /* возможен вызов функции из библиотеки агента */
                    closure()
                }
            }
        }
        binding.startTask = { taskType, closure ->
            closure.delegate = delegate

            if (binding.isStartTask) {
                closure()
                if (agent_on_end_task_provided) {
                    agentOnEndTask.call(taskType)
                } else {
                    throw new RuntimeException("Функция agentOnEndTask не загружена")
                }
            }
        }
    }

    void prepareTypes(Binding binding) {
        agentTypes.each {
            def code = it.getCode()
            binding."${getAgentTypeVariableByCode(code)}" = code
        }
        messageBodyTypes.each {
            def code = it.getCode()
            binding."${getMessageBodyTypeVariableByCode(code)}" = code
        }
        messageGoalTypes.each {
            def code = it.getCode()
            binding."${getMessageGoalTypeVariableByCode(code)}" = code
        }
        serviceMessageTypes.each {
            def code = it.getCode()
            binding."${getServiceMessageTypeVariableByCode(code)}" = code
        }
        localMessageTypes.each {
            binding."${getLocalMessageTypeVariableByCode(it)}" = it
        }
        taskTypes.each {
            binding."${getTaskTypeVariableByCode(it)}" = it
        }
        SystemEvent.values().each {
            binding."${getSystemEventTypeVariableByCode(it.code)}" = it.code
        }
    }

    /* Имена переменных словарей */
    String getAgentTypeVariableByCode(String code) {
        "${code.toUpperCase()}_AT"
    }
    String getServiceMessageTypeVariableByCode(String code) {
        "${code.toUpperCase()}_SMT"
    }
    String getMessageGoalTypeVariableByCode(String code) {
        "${code.toUpperCase()}_MGT"
    }
    String getMessageBodyTypeVariableByCode(String code) {
        "${code.toUpperCase()}_MBT"
    }
    String getLocalMessageTypeVariableByCode(String code) {
        "${code.toUpperCase()}_LMT"
    }
    String getTaskTypeVariableByCode(String code) {
        "${code.toUpperCase()}_TT"
    }
    String getSystemEventTypeVariableByCode(String code) {
        "${code.toUpperCase()}_SE"
    }

    /**
     * Параметры инициализации агента
     */
    private void prepareInitData(Binding binding) {
        binding.type = ""
        binding.name = ""
        binding.masId = ""
        binding.defaultBodyType = ""
        binding.localMessageTypes = null
        binding.taskTypes = null
    }

    /**
     * Проверка на инициализацию обязательных данных блока init
     */
    private boolean initDataIsNullOrEmpty() {
        if (agentType == null || agentType.isEmpty() || agentName == null || agentName.isEmpty() ||
                agentMasId == null || agentMasId.isEmpty() || defaultBodyType == null || defaultBodyType.isEmpty() ||
                localMessageTypes == null || taskTypes == null) {
            return true
        }
        return false
    }
}
