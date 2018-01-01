package com.mycompany.dsl.base.behavior

import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslMessage

/**
 * События, на которые может быть добавлено поведение агента
 *
 * @author Nikita Gorodilov
 */
interface IRuntimeAgentBehaviorEventSink {

    /**
     * Прикрепление поведения к агенту
     */
    fun bing(runtimeAgent: RuntimeAgent)

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
     * Вызывается перед началом события загрузки изображения
     */
    fun beforeOnLoadImage(image: DslImage)

    /**
     * Вызывается после события загрузки изображения
     */
    fun afterOnLoadImage(image: DslImage)

    /**
     * Вызывается перед началом события получения сообщения
     */
    fun beforeOnGetMessage(message: DslMessage)

    /**
     * Вызывается после события получения сообщения
     */
    fun afterOnGetMessage(message: DslMessage)

    /**
     * Вызывается перед началом события завершение работы с изображением
     */
    fun beforeOnEndImageTask(updateImage: DslImage)

    /**
     * Вызывается после события завершение работы с изображением
     */
    fun afterOnEndImageTask(updateImage: DslImage)
}