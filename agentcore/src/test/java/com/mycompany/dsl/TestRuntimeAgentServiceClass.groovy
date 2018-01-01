package com.mycompany.dsl

/**
 * @author Nikita Gorodilov
 */
class TestRuntimeAgentServiceClass extends RuntimeAgentService {

    def isExecuteInit = false
    def isExecuteTestOnGetMessages = false
    def isExecuteTestOnLoadImage = false
    def isExecuteTestOnEndImageTask = false

    def isExecuteA1_testOnGetMessageFun = false
    def isExecuteA1_testOnLoadImageFun = false
    def isExecuteA1_testOnEndImageTaskFun = false
    def isExecuteA2_testOnGetMessageFun = false
    def isExecuteA2_testOnLoadImageFun = false
    def isExecuteA2_testOnEndImageTaskFun = false

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
    }
    def a1_testOnLoadImageFun() {
        isExecuteA1_testOnLoadImageFun = true
    }
    def a1_testOnEndImageTaskFun() {
        isExecuteA1_testOnEndImageTaskFun = true
    }

    def a2_testOnGetMessageFun() {
        isExecuteA2_testOnGetMessageFun = true
    }
    def a2_testOnLoadImageFun() {
        isExecuteA2_testOnLoadImageFun = true
    }
    def a2_testOnEndImageTaskFun() {
        isExecuteA2_testOnEndImageTaskFun = true
    }
}
