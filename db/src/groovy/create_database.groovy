import groovy.sql.Sql

import java.sql.Types

// --------------- Подключение к БД ---------------
def sql = Sql.newInstance("jdbc:sqlite:" + parentDir + addrDB + nameDB, "org.sqlite.JDBC")

// todo создание views и работа с ними у агентов

def sqlScript = ""
[
        "/src/sql/delete_tables.sql",
        "/src/sql/create_tables.sql",
        "/src/sql/create_data.sql"
].each {
    new File((String) sourceDir + it).eachLine {
        sqlScript += it + "\n"
    }
}

for (script in sqlScript.split(';'))
    if (!script.toString().equals("\n"))
        sql.execute(script)

// пример получения типов данных и названий таблиц из бд sqlite
def columnTypes = [:]
def metaClosure = { metaData ->
    /* I'm called once by Sql.eachRow() with a ResultSetMetaData. */
    columnTypes = (1..metaData.columnCount).collectEntries {
        [(metaData.getColumnName(it)): metaData.getColumnType(it)]
    }
}
sql.eachRow('SELECT * FROM inputdataA', metaClosure) { row ->
    /*
     * The result set SQL types and row values are available here.
     * Examples:
     * def value = row['column_name']
     * def type = columnTypes['column_name']
     */
    def value = row['occupancyA']
    def type = columnTypes['occupancyA']
    println("value = " + value)
    println("type = " + type)
    if (type == Types.INTEGER)
        println("успех")
}