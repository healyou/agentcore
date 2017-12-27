package dsl

import dsl.objects.DslImage
import dsl.objects.DslMessage
import dsl.base.SendMessageParameters
import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

import java.awt.Image

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentService {

    def runtimeAgent

    def agentType = null
    def agentName = null
    def agentMasId = null
    def defaultBodyType = null

    List<AgentType> agentTypes = []
    List<MessageBodyType> messageBodyTypes = []
    List<MessageGoalType> messageGoalTypes = []
    List<MessageType> messageTypes = []

    boolean on_load_image_provided = false
    def onLoadImage = {}

    boolean on_get_message_provided = false
    def onGetMessage = {}

    boolean on_end_image_task_provided = false
    def onEndImageTask = {}

    boolean init_provided = false
    def init = {}

    boolean agent_send_message_provided = false
    def agentSendMessage = {}

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
        binding.onGetMessage = onGetMessage
        binding.onEndImageTask = onEndImageTask

        return binding
    }

    void setAgentSendMessageClosure(Closure c) {
        agent_send_message_provided = true
        agentSendMessage = c
    }

    void checkLoadRules(Binding binding) {
        if (bindingFunctionCheck(binding)) {
            init = binding.init
            onLoadImage = binding.onLoadImage
            onGetMessage = binding.onGetMessage
            onEndImageTask = binding.onEndImageTask

            on_load_image_provided = true
            on_get_message_provided = true
            on_end_image_task_provided = true
            init_provided = true
        } else {
            throw new RuntimeException("Неправильная dsl")
        }
    }

    /* Проверка функций */
    boolean bindingFunctionCheck(Binding binding) {
        return binding.init != init && binding.onLoadImage != onLoadImage && binding.onGetMessage != onGetMessage && binding.onEndImageTask != onEndImageTask
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

    void applyOnGetMessage(DslMessage message) {
        if (on_get_message_provided) {
            Binding binding = new Binding()

            prepareTypes(binding)
            prepareClosures(binding)

            binding.message = message

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onGetMessage.delegate = this;onGetMessage.resolveStrategy = Closure.DELEGATE_FIRST;onGetMessage(message)")
        } else {
            throw new RuntimeException("Функция on_get_message не загружена")
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
        binding.onGetMessage = onGetMessage
        binding.onEndImageTask = onEndImageTask
        binding.sendMessage = { Map map ->
            /* required fields */
            SendMessageParameters.values().each {
                if (it.required && map[it.paramName] == null) {
                    throw new RuntimeException("Обязательный параметр '" + it.paramName + "' не найден")
                }
            }

            /* default value fields */
            def bodyTypeParamName = SendMessageParameters.BODY_TYPE.paramName
            def bodyType = map[bodyTypeParamName]
            if (bodyType == null) {
                map[bodyTypeParamName] = defaultBodyType
            }

            if (agent_send_message_provided) {
                agentSendMessage.call(map)

            } else {
                throw new RuntimeException("Функция sendMessage не загружена")
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
                use(ImagesFunctions) {
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
        messageTypes.each {
            def code = it.getCode()
            binding."${getMessaTypeVariableByCode(code)}" = code
        }
    }

    /* Имена переменных словарей */
    String getAgentTypeVariableByCode(String code) {
        "${code.toUpperCase()}_AT"
    }
    String getMessaTypeVariableByCode(String code) {
        "${code.toUpperCase()}_MT"
    }
    String getMessageGoalTypeVariableByCode(String code) {
        "${code.toUpperCase()}_MGT"
    }
    String getMessageBodyTypeVariableByCode(String code) {
        "${code.toUpperCase()}_MBT"
    }

    /**
     * Параметры инициализации агента
     */
    private void prepareInitData(Binding binding) {
        binding.type = ""
        binding.name = ""
        binding.masId = ""
        binding.defaultBodyType = ""
    }

    /**
     * Проверка на инициализацию обязательных данных блока init
     */
    private boolean initDataIsNullOrEmpty() {
        if (agentType == null || agentType.isEmpty() || agentName == null || agentName.isEmpty() ||
                agentMasId == null || agentMasId.isEmpty() || defaultBodyType == null || defaultBodyType.isEmpty()) {
            true
        }
        false
    }
}
