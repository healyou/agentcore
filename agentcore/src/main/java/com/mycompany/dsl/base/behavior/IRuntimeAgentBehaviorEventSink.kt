package com.mycompany.dsl.base.behavior

import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData

/**
 * События, на которые может быть добавлено поведение агента
 *
 * @author Nikita Gorodilov
 */
interface IRuntimeAgentBehaviorEventSink {

    /**
     * Прикрепление поведения к агенту
     */
    fun bind(runtimeAgent: RuntimeAgent)

    /**
     * Открепление поведения от агенту
     */
    fun unbind()

    /**
     * Начало работы агента
     */
    fun onStart()

    /**
     * Завершение работы агента
     */
    fun onStop()

    /**
     * Вызывается перед началом события получения сервисного сообщения
     */
    fun beforeOnGetServiceMessage(serviceMessage: DslServiceMessage)

    /**
     * Вызывается после события получения сервисного сообщения
     */
    fun afterOnGetServiceMessage(serviceMessage: DslServiceMessage)

    /**
     * Вызывается после события получения локального сообщения
     */
    fun beforeOnGetLocalMessage(localMessage: DslLocalMessage)

    /**
     * Вызывается после события получения локального сообщения
     */
    fun afterOnGetLocalMessage(localMessage: DslLocalMessage)

    /**
     * Вызывается до начала вызова функции завершения задачи
     */
    fun beforeOnEndTask(taskData: DslTaskData)

    /**
     * Вызывается после вызова функции завершения задачи
     */
    fun afterOnEndTask(taskData: DslTaskData)
}