package db.base

import service.objects.AgentType
import spock.lang.Specification

/**
 * @author Nikita Gorodilov
 */
class ExtensionsSpecification extends Specification {

    def "Не пустая строка должна правильно конвертироваться в массив типов AgentType.Code"() {
        when:
        def list
        use(ExtensionsKt) {
            list = VALUE.sqlite_toAgentCodes()
        }

        then:
        EXPECT.each { itExpect ->
            assert list.stream().anyMatch { it -> itExpect == it }
        }

        where:
        [EXPECT, VALUE] << [
                [Arrays.asList(AgentType.Code.WORKER, AgentType.Code.SERVER), AgentType.Code.WORKER.code + "!" + AgentType.Code.SERVER.code],
                [Arrays.asList(AgentType.Code.SERVER), AgentType.Code.SERVER.code + "!"],
                [Arrays.asList(AgentType.Code.SERVER), AgentType.Code.SERVER.code]
        ]
    }

    def "Пустая строка должна правильно конвертироваться в массив типов AgentType.Code"() {
        when:
        def list = null
        def value = ""
        use(ExtensionsKt) {
            list = value.sqlite_toAgentCodes()
        }

        then:
        noExceptionThrown()
        assert list.isEmpty()
    }

    def "Массив типов AgentType.Code должен правильно конвертироваться в строку"() {
        when:
        def sqliteString = null
        use(ExtensionsKt) {
            sqliteString = VALUE.toSqlite()
        }

        then:
        assert EXPECT == sqliteString

        where:
        [EXPECT, VALUE] << [
                [AgentType.Code.WORKER.code + "!" + AgentType.Code.SERVER.code, Arrays.asList(AgentType.Code.WORKER, AgentType.Code.SERVER)],
                [AgentType.Code.SERVER.code, Arrays.asList(AgentType.Code.SERVER)],
                ["", Collections.emptyList()]
        ]
    }
}
