package com.mycompany.dsl

import com.mycompany.agentworklibrary.AAgentWorkLibrary
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.base.SendServiceMessageParameters
import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.MessageBodyType
import com.mycompany.service.objects.MessageGoalType
import com.mycompany.service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentService {

    def runtimeAgent

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

    boolean on_load_image_provided = false
    def onLoadImage = {}
    boolean on_get_service_message_provided = false
    def onGetServiceMessage = {}
    boolean on_get_local_message_provided = false
    def onGetLocalMessage = {}
    boolean on_end_image_task_provided = false
    def onEndImageTask = {}
    boolean init_provided = false
    def init = {}
    boolean agent_send_message_provided = false
    def agentSendServiceMessage = {}

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
        binding.onLoadImage = onLoadImage
        binding.onGetServiceMessage = onGetServiceMessage
        binding.onGetLocalMessage = onGetLocalMessage
        binding.onEndImageTask = onEndImageTask

        return binding
    }

    void setAgentSendMessageClosure(Closure c) {
        agent_send_message_provided = true
        agentSendServiceMessage = c
    }

    void checkLoadRules(Binding binding) {
        if (bindingFunctionCheck(binding)) {
            init = binding.init
            onLoadImage = binding.onLoadImage
            onGetServiceMessage = binding.onGetServiceMessage
            onGetLocalMessage = binding.onGetLocalMessage
            onEndImageTask = binding.onEndImageTask

            on_load_image_provided = true
            on_get_service_message_provided = true
            on_get_local_message_provided = true
            on_end_image_task_provided = true
            init_provided = true
        } else {
            throw new RuntimeException("Неправильная dsl")
        }
    }

    /* Проверка функций */
    boolean bindingFunctionCheck(Binding binding) {
        return binding.init != init && binding.onLoadImage != onLoadImage &&
                binding.onGetServiceMessage != onGetServiceMessage && binding.onEndImageTask != onEndImageTask &&
                binding.onGetLocalMessage != onGetLocalMessage
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

    void applyOnLoadImage(DslImage image) {
        if (on_load_image_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.image = image

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onLoadImage.delegate = this;onLoadImage.resolveStrategy = Closure.DELEGATE_FIRST;onLoadImage(image)")
        } else {
            throw new RuntimeException("Функция on_load_image не загружена")
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

    void applyOnEndImageTask(DslImage updateImage) {
        if (on_end_image_task_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.updateImage = updateImage

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onEndImageTask.delegate = this;onEndImageTask.resolveStrategy = Closure.DELEGATE_FIRST;onEndImageTask(updateImage)")
        } else {
            throw new RuntimeException("Функция on_end_image_task не загружена")
        }
    }

    private void prepareClosures(Binding binding) {
        binding.init = init
        binding.onLoadImage = onLoadImage
        binding.onGetServiceMessage = onGetServiceMessage
        binding.onGetLocalMessage = onGetLocalMessage
        binding.onEndImageTask = onEndImageTask
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

            if (binding.result)
                use(agentWorkLibraryClass) {
                    closure()
                }
        }
    }

    private void prepareTypes(Binding binding) {
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
