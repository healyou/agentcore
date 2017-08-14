import groovy.sql.Sql

import java.sql.Types

// --------------- Подключение к input БД ---------------
def sql = Sql.newInstance("jdbc:sqlite:" + parentDir + addrDB_input + nameDB_input, "org.sqlite.JDBC")

// todo создание views и работа с ними у агентов

def sqlScript = ""
[
        "/src/sql/inputdb/delete_tables.sql",
        "/src/sql/inputdb/create_tables.sql",
        "/src/sql/inputdb/create_data.sql"
].each {
    new File((String) sourceDir + it).eachLine {
        sqlScript += it + "\n"
    }
}

for (script in sqlScript.split(';'))
    if (script.toString() != "\n")
        sql.execute(script)