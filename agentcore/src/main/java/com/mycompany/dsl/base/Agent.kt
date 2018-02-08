package com.mycompany.dsl.base

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.dsl.base.behavior.IRuntimeAgentBehaviorEventSink

/**
 * @author Nikita Gorodilov
 */
interface Agent {

    fun start()

    fun stop()

    fun isStarted(): Boolean

    fun getSystemAgent(): SystemAgent

    fun add(behavior: IRuntimeAgentBehaviorEventSink)

    fun delete(behavior: IRuntimeAgentBehaviorEventSink)
}