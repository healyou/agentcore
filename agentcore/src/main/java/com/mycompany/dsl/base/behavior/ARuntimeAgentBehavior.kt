package com.mycompany.dsl.base.behavior

import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData

/**
 * @author Nikita Gorodilov
 */
abstract class ARuntimeAgentBehavior : IRuntimeAgentBehaviorEventSink {

    override fun bind(runtimeAgent: RuntimeAgent) {
    }

    override fun unbind() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun beforeOnGetServiceMessage(serviceMessage: DslServiceMessage) {
    }

    override fun afterOnGetServiceMessage(serviceMessage: DslServiceMessage) {
    }

    override fun beforeOnGetLocalMessage(localMessage: DslLocalMessage) {
    }

    override fun afterOnGetLocalMessage(localMessage: DslLocalMessage) {
    }

    override fun beforeOnEndTask(taskData: DslTaskData) {
    }

    override fun afterOnEndTask(taskData: DslTaskData) {
    }
}