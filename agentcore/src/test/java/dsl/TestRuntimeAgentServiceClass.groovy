package dsl

/**
 * @author Nikita Gorodilov
 */
class TestRuntimeAgentServiceClass extends RuntimeAgentService {

    def isExecuteInit = false
    def isExecuteOnGetMessages = false
    def isExecuteOnLoadImage = false
    def isExecuteOnEndImageTask = false

    /* Повторяет функцию loadExecuteRules - но тут сразу строку передаем а не файл*/
    void testLoadExecuteRules(rules) {
        Binding binding = createLoadBindings()

        GroovyShell shell = new GroovyShell(binding)
        shell.evaluate(String.valueOf(rules))

        checkLoadRules(binding)
    }

    @Override
    void applyInit() {
        super.applyInit()
        isExecuteInit = true
    }

    /**
     * Функции - вызываемые из DSL
     */

    void testOnGetMessageFun() {
        isExecuteOnGetMessages = true
    }

    void testOnLoadImage() {
        isExecuteOnLoadImage = true
    }

    void testOnEndImageTask() {
        isExecuteOnEndImageTask = true
    }
}
