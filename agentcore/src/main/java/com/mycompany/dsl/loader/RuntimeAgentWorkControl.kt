package com.mycompany.dsl.loader

import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.dsl.objects.DslImage

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentWorkControl: IRuntimeAgentWorkControl {

    override fun start() {
        TODO("not implemented")
    }

    override fun stop() {
        TODO("not implemented")
    }

    override fun start(agent: SystemAgent) {
        TODO("not implemented")
    }

    override fun stop(agent: SystemAgent) {
        TODO("not implemented")
    }

    override fun isStarted(agent: SystemAgent): Boolean {
        TODO("not implemented")
    }

    override fun onLoadImage(agent: SystemAgent, image: DslImage) {
        TODO("not implemented")
    }
}