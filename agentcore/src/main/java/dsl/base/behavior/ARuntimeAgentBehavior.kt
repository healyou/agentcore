package dsl.base.behavior

import db.core.systemagent.SystemAgent
import dsl.RuntimeAgent
import dsl.objects.DslImage
import dsl.objects.DslMessage

/**
 * @author Nikita Gorodilov
 */
abstract class ARuntimeAgentBehavior : IRuntimeAgentBehaviorEventSink {

    override fun bing(runtimeAgent: RuntimeAgent) {
    }

    override fun unbind() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun beforeOnLoadImage(image: DslImage) {
    }

    override fun afterOnLoadImage(image: DslImage) {
    }

    override fun beforeOnGetMessage(message: DslMessage) {
    }

    override fun afterOnGetMessage(message: DslMessage) {
    }

    override fun beforeOnEndImageTask(updateImage: DslImage) {
    }

    override fun afterOnEndImageTask(updateImage: DslImage) {
    }
}