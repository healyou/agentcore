package com.mycompany.integration

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.dsl.RuntimeAgent
import com.mycompany.dsl.base.SystemEvent
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData
import org.jetbrains.annotations.NotNull

/**
 * @author Nikita Gorodilov
 */
abstract class TestExecuteSequenceRuntimeAgent extends RuntimeAgent {

    public TestExecuteSequenceRuntimeAgent(DslFileAttachment dslFileAttachment) {
        super(dslFileAttachment)
    }

    private AgentExecuteSequence executeSeq

    public void withAgentExecuteSequence(AgentExecuteSequence executeSequence) {
        executeSeq = executeSequence
    }

    @Override
    void onGetSystemEvent(@NotNull SystemEvent systemEvent) {
        executeSeq.addExecutedFunction(AgentExecuteSequence.AgentExecuteFunction.ON_GET_SYSTEM_EVENT)
        super.onGetSystemEvent(systemEvent)
    }

    @Override
    void onGetLocalMessage(@NotNull DslLocalMessage localMessage) {
        executeSeq.addExecutedFunction(AgentExecuteSequence.AgentExecuteFunction.ON_GET_LOCAL_MESSAGE)
        super.onGetLocalMessage(localMessage)
    }

    @Override
    void onEndTask(@NotNull DslTaskData taskData) {
        executeSeq.addExecutedFunction(AgentExecuteSequence.AgentExecuteFunction.ON_END_TASK)
        super.onEndTask(taskData)
    }

    @Override
    void onGetServiceMessage(@NotNull DslServiceMessage serviceMessage) {
        executeSeq.addExecutedFunction(AgentExecuteSequence.AgentExecuteFunction.ON_GET_SERVICE_MESSAGE)
        super.onGetServiceMessage(serviceMessage)
    }
}
