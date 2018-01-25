package com.mycompany.dsl.loader

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.dsl.exceptions.RuntimeAgentException
import com.mycompany.dsl.objects.DslImage

/**
 * Контроль за работой агентов в системе
 *
 * @author Nikita Gorodilov
 */
interface IRuntimeAgentWorkControl {

    /**
     * Запуск всех агентов, которых можно запустить в системе
     */
    fun start()

    /**
     * Остановка выполнения всех агентов в системе
     */
    fun stop()

    /**
     * Начало работы указанного агента
     */
    @Throws(RuntimeAgentException::class)
    fun start(agent: SystemAgent)

    /**
     * Окончание выполнения работы указанного агента
     */
    @Throws(RuntimeAgentException::class)
    fun stop(agent: SystemAgent)

    /**
     * @return true - указанный агент уже выполняет работу
     */
    fun isStarted(agent: SystemAgent): Boolean

    /**
     * @return true - указанный агент может начать работу, false - агенту не хватает данных для начала работы
     */
    fun isStart(agent: SystemAgent): Boolean
}