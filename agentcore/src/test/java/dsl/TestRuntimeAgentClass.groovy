package dsl

import db.core.file.dslfile.DslFileAttachment

/**
 * @author Nikita Gorodilov
 */
abstract class TestRuntimeAgentClass extends RuntimeAgent {

    TestRuntimeAgentClass(DslFileAttachment dslFileAttachment) {
        super(dslFileAttachment)
    }

    @Override
    protected RuntimeAgentService createRuntimeAgentService() {
        return new TestRuntimeAgentServiceClass()
    }
}
