package db.base

import objects.TypesObjects
import spock.lang.Specification

/**
 * @author Nikita Gorodilov
 */
class ExtensionsSpecification extends Specification {

    def "Не пустая строка должна правильно конвертироваться в массив типов String"() {
        when:
        def list
        use(ExtensionsKt) {
            list = VALUE.sqlite_toAgentCodes()
        }

        then:
        EXPECT.each { itExpect ->
            assert list.any { it -> itExpect == it }
        }

        where:
        [EXPECT, VALUE] << [
                [Arrays.asList(TypesObjects.testAgent1TypeCode(), TypesObjects.testAgent2TypeCode()), TypesObjects.testAgent1TypeCode() + "!" + TypesObjects.testAgent2TypeCode()],
                [Arrays.asList(TypesObjects.testAgent1TypeCode()), TypesObjects.testAgent1TypeCode() + "!"],
                [Arrays.asList(TypesObjects.testAgent2TypeCode()), TypesObjects.testAgent2TypeCode()]
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
                [TypesObjects.testAgent1TypeCode() + "!" + TypesObjects.testAgent2TypeCode(), Arrays.asList(TypesObjects.testAgent1TypeCode(), TypesObjects.testAgent2TypeCode())],
                [TypesObjects.testAgent1TypeCode(), Arrays.asList(TypesObjects.testAgent1TypeCode())],
                ["", Collections.emptyList()]
        ]
    }
}
