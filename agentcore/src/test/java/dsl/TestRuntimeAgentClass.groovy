package dsl

/**
 * @author Nikita Gorodilov
 */
abstract class TestRuntimeAgentClass extends RuntimeAgent {

    TestRuntimeAgentClass(String path) {
        super(path)
    }

    @Override
    protected RuntimeAgentService createRuntimeAgentService() {
        return new TestRuntimeAgentServiceClass()
    }
}
