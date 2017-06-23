import groovy.sql.Sql

// --------------- Подключение к БД ---------------
def final sql = Sql.newInstance("jdbc:sqlite:" + parentDir + addrDB + nameDB, "org.sqlite.JDBC")

// удаление таблиц
//def tables = [
//        "inputdataA",
//        "inputdataB",
//        "inputdataC"
//]
//def names = []
//sql.eachRow("select name from sqlite_master where type is 'table';") { table ->
//    println(table)
//    names.add(table.name)
//}
//names.forEach() { tableName ->
//    if (tables.contains(tableName)) {
//        def sqlString = "drop table if exists ?"
//        println(sqlString)
//        sql.execute(sqlString, tableName)
//    }
//}
//
////sql.execute("drop table if exists inputdataA")

def sqlScript = ""
// Создание таблиц и заполнение начальными данными
[
        "/src/sql/delete_tables.sql",
        "/src/sql/create_tables.sql"
        //"/src/install/create_data.sql"
].each {
    new File((String) sourceDir + it).eachLine {
        sqlScript += it + "\n"
    }
}

println(sqlScript)
if (!sqlScript.equals(""))
    sql.execute(sqlScript)
sql.close()