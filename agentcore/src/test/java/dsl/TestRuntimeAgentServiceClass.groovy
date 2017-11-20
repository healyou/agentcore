package dsl

/**
 * @author Nikita Gorodilov
 */
class TestRuntimeAgentServiceClass extends RuntimeAgentService {

    def isExecuteInit = false
    def isExecuteTestOnGetMessages = false
    def isExecuteTestOnLoadImage = false
    def isExecuteTestOnEndImageTask = false

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
        isExecuteTestOnGetMessages = true
    }

    void testOnLoadImage() {
        isExecuteTestOnLoadImage = true
    }

    void testOnEndImageTask() {
        isExecuteTestOnEndImageTask = true
    }
}
