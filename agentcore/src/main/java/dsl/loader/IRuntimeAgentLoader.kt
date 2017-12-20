package dsl.loader

import db.core.systemagent.SystemAgent
import dsl.ThreadPoolRuntimeAgent
import dsl.objects.DslImage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgentLoader {

    fun load(createAgent: (path: String) -> ThreadPoolRuntimeAgent)
    fun start()
    fun stop()
    fun onLoadImage(agent: SystemAgent, image: DslImage)
}