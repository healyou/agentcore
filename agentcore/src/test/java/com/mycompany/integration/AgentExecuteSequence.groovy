package com.mycompany.integration

/**
 * @author Nikita Gorodilov
 */
/**
 * Проверка последовательности выполнения функций тестовыми агентами
 */
public class AgentExecuteSequence {
    private List<AgentExecuteFunction> executeSequence = new ArrayList<>()

    AgentExecuteSequence() {
    }

    AgentExecuteSequence(AgentExecuteFunction... executeFunctions) {
        this.executeSequence = executeFunctions
    }

    enum AgentExecuteFunction {
        ON_GET_SYSTEM_EVENT,
        ON_GET_LOCAL_MESSAGE,
        ON_END_TASK,
        ON_GET_SERVICE_MESSAGE
    }

    public void addExecutedFunction(AgentExecuteFunction executeFunction) {
        this.executeSequence.add(executeFunction)
    }

    public void clear() {
        this.executeSequence.clear()
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) {
            return false
        }

        if (obj instanceof AgentExecuteSequence) {
            def objSeq = obj as AgentExecuteSequence
            def objExecuteSequence = objSeq.executeSequence
            return equalsExecuteSequence(objExecuteSequence)
        } else {
            return false
        }
    }

    private boolean equalsExecuteSequence(List<AgentExecuteFunction> objExecuteSequence) {
        if (this.executeSequence.size() != objExecuteSequence.size()) {
            return false

        } else {
            return objExecuteSequence.stream().allMatch { objExecuteFunction ->
                def seqIndex = this.executeSequence.indexOf(objExecuteFunction)
                return this.executeSequence[seqIndex] == objExecuteFunction
            }
        }
    }
}