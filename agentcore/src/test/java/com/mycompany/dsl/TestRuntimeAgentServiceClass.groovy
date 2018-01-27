package com.mycompany.dsl

/**
 * @author Nikita Gorodilov
 */
class TestRuntimeAgentServiceClass extends RuntimeAgentService {

    def isExecuteInit = false
    def isExecuteTestOnGetServiceMessages = false
    def isExecuteTestOnGetLocalMessages = false
    def isExecuteTestOnEndTask = false
    def isExecuteTestOnGetSystemEvent = false

    def isExecuteA1_testOnGetServiceMessageFun = false
    def isExecuteA1_testOnGetLocalMessageFun = false
    def isExecuteA1_testOnEndTask = false
    def isExecuteA1_testOnGetSystemEvent = false
    def isExecuteA2_testOnGetServiceMessageFun = false
    def isExecuteA2_testOnGetLocalMessageFun = false
    def isExecuteA2_testOnEndTask = false
    def isExecuteA2_testOnGetSystemEvent = false

    /**
     * @param isCreateOtherFunctions true - будут созданы пустые доп. фунции
     */
    TestRuntimeAgentServiceClass(isCreateEmptyOtherFunctions = false) {
        if (isCreateEmptyOtherFunctions) {
            configureEmptyOtherFunctions()
        }
    }

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
     * Заполняем агента пустыми дополнительными функциями(нужны для работы агента)
     */
    private void configureEmptyOtherFunctions() {
        setAgentOnEndTaskClosure {}
        setAgentSendMessageClosure {}
        setConfigureAgentDataClosure { return null }
    }

    /**
     * Функции - вызываемые из DSL
     */

    void testOnGetServiceMessageFun() {
        isExecuteTestOnGetServiceMessages = true
    }
    void testOnGetLocalMessageFun() {
        isExecuteTestOnGetLocalMessages = true
    }
    void testOnEndTask() {
        isExecuteTestOnEndTask = true
    }
    void testOnGetSystemEvent() {
        isExecuteTestOnGetSystemEvent = true
    }

    /**
     * Методы для тестирования работы двух агентов без интернета(инет - тестовые объекты)
     */

    def a1_testOnGetServiceMessageFun() {
        isExecuteA1_testOnGetServiceMessageFun = true
    }
    def a1_testOnGetLocalMessageFun() {
        isExecuteA1_testOnGetLocalMessageFun = true
    }
    def a1_testOnEndTask() {
        isExecuteA1_testOnEndTask = true
    }
    def a1_testOnGetSystemEvent() {
        isExecuteA1_testOnGetSystemEvent = true
    }

    def a2_testOnGetServiceMessageFun() {
        isExecuteA2_testOnGetServiceMessageFun = true
    }
    def a2_testOnGetLocalMessageFun() {
        isExecuteA2_testOnGetLocalMessageFun = true
    }
    def a2_testOnEndTask() {
        isExecuteA2_testOnEndTask = true
    }
    def a2_testOnGetSystemEvent() {
        isExecuteA2_testOnGetSystemEvent = true
    }
}
