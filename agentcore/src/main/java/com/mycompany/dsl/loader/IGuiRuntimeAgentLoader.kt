package com.mycompany.dsl.loader

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.objects.DslImage

/**
 * @author Nikita Gorodilov
 */
interface IGuiRuntimeAgentLoader {

    fun load(createAgent: (dslFile: DslFileAttachment) -> ThreadPoolRuntimeAgent)
    fun start()
    fun stop()
    fun onLoadImage(agent: SystemAgent, image: DslImage)
}