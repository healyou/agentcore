package com.mycompany.dsl

import com.mycompany.db.core.file.dslfile.DslFileAttachment

/**
 * @author Nikita Gorodilov
 */
abstract class TestRuntimeAgentClass extends RuntimeAgent {

    TestRuntimeAgentClass(String serviceLogin) {
        super(serviceLogin);
    }

    TestRuntimeAgentClass(DslFileAttachment dslFileAttachment) {
        super(dslFileAttachment)
    }

    @Override
    protected RuntimeAgentService createRuntimeAgentService() {
        return new TestRuntimeAgentServiceClass()
    }
}
