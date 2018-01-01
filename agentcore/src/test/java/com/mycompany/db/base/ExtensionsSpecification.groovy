package com.mycompany.db.base

import objects.OtherObjects
import objects.StringObjects
import objects.TypesObjects
import spock.lang.Specification

import java.text.ParseException

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

    def "Sqlite строка с датой правильно конвертируется в Date"() {
        Date date = OtherObjects.getDate(2016, 10, 26)

        when:
        Date convertDate = null
        use(ExtensionsKt) {
            convertDate = OtherObjects.getSqliteDateString(date).fromSqlite()
        }

        then:
        assert date.getTime() == convertDate.getTime()

        when:
        convertDate = null
        use(ExtensionsKt) {
            convertDate = "error date string".fromSqlite()
        }

        then:
        thrown ParseException
    }

    def "Date правильно конвертируется в Sqlite строку"() {
        Date date = OtherObjects.getDate(2016, 10, 26)

        when:
        String convertString = null
        use(ExtensionsKt) {
            convertString = date.toSqlite()
        }

        then:
        assert OtherObjects.getSqliteDateString(date) == convertString
    }

    def "isDeleted строка в бд правильно конвертируется в boolean"() {
        when:
        Boolean convertBoolean = null
        use(ExtensionsKt) {
            convertBoolean = VALUE.sqlite_toBoolean()
        }

        then:
        assert EXPECT == convertBoolean

        where:
        EXPECT | VALUE
        true   | ExtensionsKt.SQLITE_YES_STRING
        false  | ExtensionsKt.SQLITE_NO_STRING
    }

    def "Неверный формат строки isDeleted sqlite вызывает ошибку при конвертации в boolean"() {
        when:
        Boolean convertBoolean = null
        use(ExtensionsKt) {
            convertBoolean = StringObjects.randomString().sqlite_toBoolean()
        }

        then:
        thrown UnsupportedOperationException
    }

    def "boolean правильно конвертируется в isDeleted строку в бд"() {
        when:
        String convertString = ExtensionsKt.toSqlite(VALUE)

        then:
        assert EXPECT == convertString

        where:
        EXPECT                         | VALUE
        ExtensionsKt.SQLITE_YES_STRING | true
        ExtensionsKt.SQLITE_NO_STRING  | false
    }
}
