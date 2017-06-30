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