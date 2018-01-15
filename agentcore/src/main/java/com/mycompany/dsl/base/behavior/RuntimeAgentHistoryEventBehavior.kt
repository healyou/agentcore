package com.mycompany.dsl.base.behavior

import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslMessage

/**
 * Сохранение истории действий агента
 *
 * @author Nikita Gorodilov
 */
open class RuntimeAgentHistoryEventBehavior(private val historyService: SystemAgentEventHistoryService): ARuntimeAgentBehavior() {

    protected lateinit var runtimeAgent: RuntimeAgent

    override fun bind(runtimeAgent: RuntimeAgent) {
        super.bind(runtimeAgent)
        this.runtimeAgent = runtimeAgent
        onEvent("Добавление RuntimeAgentHistoryEventBehavior к агенту")
    }

    override fun unbind() {
        super.unbind()
        onEvent("Отсоединение RuntimeAgentHistoryEventBehavior от агента")
    }

    override fun onStart() {
        super.onStart()
        onEvent("Начало работы агента")
    }

    override fun onStop() {
        super.onStop()
        onEvent("Конец работы агента")
    }

    override fun beforeOnLoadImage(image: DslImage) {
        super.beforeOnLoadImage(image)
        onEvent("Начало dsl функции onLoadImage")
    }

    override fun afterOnLoadImage(image: DslImage) {
        super.afterOnLoadImage(image)
        onEvent("Конец dsl функции onLoadImage")
    }

    override fun beforeOnGetMessage(message: DslMessage) {
        super.beforeOnGetMessage(message)
        onEvent("Начало dsl функции onGetMessage")
    }

    override fun afterOnGetMessage(message: DslMessage) {
        super.afterOnGetMessage(message)
        onEvent("Конец dsl функции onGetMessage")
    }

    override fun beforeOnEndImageTask(updateImage: DslImage) {
        super.beforeOnEndImageTask(updateImage)
        onEvent("Начало dsl функции onEndImageTask")
    }

    override fun afterOnEndImageTask(updateImage: DslImage) {
        super.afterOnEndImageTask(updateImage)
        onEvent("Конец dsl функции onEndImageTask")
    }

    open protected fun onEvent(message: String) {
        createSystemAgentHistory(message)
    }

    private fun createSystemAgentHistory(message: String) {
        historyService.create(
                SystemAgentEventHistory(
                        getSystemAgentId(),
                        message
                )
        )
    }

    private fun getSystemAgentId(): Long {
        return runtimeAgent.getSystemAgent().id!!
    }
}