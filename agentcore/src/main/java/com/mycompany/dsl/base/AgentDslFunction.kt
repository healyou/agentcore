package com.mycompany.dsl.base

import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData

/**
 * События, действия которых описываются через dsl файл конфигурации агента
 *
 * @author Nikita Gorodilov
 */
interface AgentDslFunction {

    fun onGetServiceMessage(serviceMessage: DslServiceMessage)

    fun onGetLocalMessage(localMessage: DslLocalMessage)

    fun onEndTask(taskData: DslTaskData)

    fun onGetSystemEvent(systemEvent: SystemEvent)
}