package dsl.loader

import db.core.file.dslfile.DslFileAttachment
import db.core.systemagent.SystemAgent
import dsl.ThreadPoolRuntimeAgent
import dsl.objects.DslImage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgentLoader {

    fun load(createAgent: (dslFile: DslFileAttachment) -> ThreadPoolRuntimeAgent)
    fun start()
    fun stop()
    fun onLoadImage(agent: SystemAgent, image: DslImage)
}