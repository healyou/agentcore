package dsl;

import db.core.servicemessage.ServiceMessage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Класс java, тк использующий его kotlin класс ничего не должен знать про groovy
 * kotlin файлы ничего не должны знать про groovy классы
 *
 * @author Nikita Gorodilov
 */
public abstract class RuntimeAgent extends ARuntimeAgent {

    private RuntimeAgentService runtimeAgentService = new RuntimeAgentService();

    public RuntimeAgent(String path) {
        super();
        runtimeAgentService.loadExecuteRules(path);
        runtimeAgentService.applyInit();
    }

    @Override
    public void onLoadImage(@Nullable Image image) {
        runtimeAgentService.applyOnLoadImage(image);
    }

    @Override
    public void onGetMessage(@NotNull ServiceMessage serviceMessage) {
        runtimeAgentService.applyOnGetMessage(serviceMessage);
    }

    @Override
    public void onEndImageTask(@Nullable Image updateImage) {
        runtimeAgentService.applyOnEndImageTask(updateImage);
    }
}
