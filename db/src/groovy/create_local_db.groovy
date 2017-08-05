import groovy.sql.Sql

// --------------- Подключение к input БД ---------------
def sql = Sql.newInstance("jdbc:sqlite:" + parentDir + addrDB_local + nameDB_local, "org.sqlite.JDBC")

// todo создание views и работа с ними у агентов

def sqlScript = ""
[
        "/src/sql/localdb/delete_tables.sql",
        "/src/sql/localdb/create_tables.sql",
        "/src/sql/localdb/create_data.sql"
].each {
    new File((String) sourceDir + it).eachLine {
        sqlScript += it + "\n"
    }
}

for (script in sqlScript.split(';'))
    if (script.toString() != "\n")
        sql.execute(script)