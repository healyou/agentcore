package dsl

import db.core.servicemessage.ServiceMessage

import java.awt.Image

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentService {

    def agentType = null
    def agentName = null
    def masId = null

    boolean on_load_image_provided = true
    def onLoadImage = { image ->
        on_load_image_provided = false
    }

    boolean on_get_message_provided = true
    def onGetMessage = { serviceMessage ->
        on_get_message_provided = false
    }

    boolean on_end_image_task_provided = true
    def onEndImageTask = { updateImage ->
        on_end_image_task_provided = false
    }

    boolean init_provided = true
    def init = {
        init_provided = false
    }

    void loadExecuteRules(path) {
        Binding binding = new Binding()

        binding.init = init
        binding.onLoadImage = onLoadImage
        binding.onGetMessage = onGetMessage
        binding.onEndImageTask = onEndImageTask

        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(new File(String.valueOf(path)))

        init = binding.init
        onLoadImage = binding.onLoadImage
        onGetMessage = binding.onGetMessage
        onEndImageTask = binding.onEndImageTask
    }

    void applyInit() {
        if (init_provided) {
            Binding binding = new Binding()
            prepareInitData(binding)
            binding.init = init

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("init.delegate = this;init()")

            agentType = binding.type
            agentName = binding.name
            masId = binding.masId

            println("masId from groovy " + masId)
        }
    }

    void applyOnLoadImage(Image image) {
        if (on_load_image_provided) {
            Binding binding = new Binding()
            prepareInitData(binding)
            prepareClosures(binding)
            prepareAgentTypes(binding)

            binding.image = image

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onLoadImage.delegate = this;onLoadImage(image)")
        }
    }
    void applyOnGetMessage(ServiceMessage serviceMessage) {
        if (on_get_message_provided) {
            Binding binding = new Binding()
            prepareClosures(binding)
            prepareAgentTypes(binding)

            binding.serviceMessage = serviceMessage

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onGetMessage.delegate = this;onGetMessage(serviceMessage)")
        }
    }
    void applyOnEndImageTask(Image updateImage) {
        if (on_end_image_task_provided) {
            Binding binding = new Binding()
            prepareClosures(binding)
            prepareAgentTypes(binding)

            binding.updateImage = updateImage

            GroovyShell shell = new GroovyShell(binding)
            shell.evaluate("onEndImageTask.delegate = this;onEndImageTask(updateImage)")
        }
    }

    private void prepareClosures(Binding binding) {
        binding.init = init
        binding.onLoadImage = onLoadImage
        binding.onGetMessage = onGetMessage
        binding.onEndTask = onEndImageTask
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

            binding.result = true // Starting premise is true
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
                closure()
        }
    }

    private void prepareAgentTypes(Binding binding) {
        /* пока типы агентов задаются статично - тк они находятся все в сервисе обмена сообщениями */
        binding.WORKER = "worker"
        binding.SERVER = "server"
    }

    /**
     * Параметры инициализации агента
     */
    private void prepareInitData(Binding binding) {
        binding.type = "testworker"
        binding.name = "testname"
        binding.masId = "test" + UUID.randomUUID()
    }
}
