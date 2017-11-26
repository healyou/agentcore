package dsl

/**
 * @author Nikita Gorodilov
 */
class TestRuntimeAgentServiceClass extends RuntimeAgentService {

    def isExecuteInit = false
    def isExecuteTestOnGetMessages = false
    def isExecuteTestOnLoadImage = false
    def isExecuteTestOnEndImageTask = false

    def isExecuteA1_testOnGetMessageFun = false
    def isExecuteA1_testOnLoadImage = false
    def isExecuteA1_testOnEndImageTask = false
    def isExecuteA2_testOnGetMessageFun = false
    def isExecuteA2_testOnLoadImage = false
    def isExecuteA2_testOnEndImageTask = false

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

    /**
     * Методы для тестирования работы двух агентов без интернета(инет - тестовые объекты)
     */

    def a1_testOnGetMessageFun() {
        isExecuteA1_testOnGetMessageFun = true
        def k = 1
    }
    def a1_testOnLoadImage() {
        isExecuteA1_testOnLoadImage = true
    }
    def a1_testOnEndImageTask() {
        isExecuteA1_testOnEndImageTask = true
    }

    def a2_testOnGetMessageFun() {
        isExecuteA2_testOnGetMessageFun = true
        def k = 1
    }
    def a2_testOnLoadImage() {
        isExecuteA2_testOnLoadImage = true
    }
    def a2_testOnEndImageTask() {
        isExecuteA2_testOnEndImageTask = true
    }
}
