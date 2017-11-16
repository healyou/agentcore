package dsl

import db.core.servicemessage.ServiceMessage
import org.jetbrains.annotations.NotNull

import java.awt.Image

/**
 * @author Nikita Gorodilov
 */
abstract class RuntimeAgent extends ARuntimeAgent {

    private def runtimeAgentService = new RuntimeAgentService()

    RuntimeAgent(String path) {
        super()
        runtimeAgentService.loadExecuteRules(path)
        runtimeAgentService.applyInit()
    }

    @Override
    void onLoadImage(Image image) {
        runtimeAgentService.applyOnLoadImage(image)
    }

    @Override
    void onGetMessage(@NotNull ServiceMessage serviceMessage) {
        runtimeAgentService.applyOnGetMessage(serviceMessage)
    }

    @Override
    void onEndImageTask(Image updateImage) {
        runtimeAgentService.onEndImageTask(updateImage)
    }
}
