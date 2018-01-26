package com.mycompany.dsl.base

import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun start()

    fun stop()

    fun onGetServiceMessage(serviceMessage: DslServiceMessage)

    fun onGetLocalMessage(localMessage: DslLocalMessage)

    fun onEndTask(taskData: DslTaskData)

    fun onGetSystemEvent(systemEvent: SystemEvent)
}