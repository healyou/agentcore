import groovy.sql.Sql

// --------------- Подключение за пользователя, выполняющего удаление старой БД ---------------
sql = Sql.newInstance("jdbc:postgresql:" + addrDB + "postgres", postgresUser, postgresPassword, "org.postgresql.Driver")

// Выкидываются насильно все пользователи с БД. Юзер, выполняющий скрипт, должен иметь на это права.
sql.execute("select pg_terminate_backend(pg_stat_activity.pid) from pg_stat_activity where pg_stat_activity.datname = '" + nameDB + "' and pid <> pg_backend_pid()")

// Удаляются БД и её юзер
sql.execute("drop database if exists " + nameDB)
sql.execute("drop user if exists " + userDB)

// Создаются заново БД и её юзерп, юзеру даются права
sql.execute("create database " + nameDB)
sql.execute("create user " + userDB + " with password '" + passDB + "'")
sql.execute("grant connect on database " + nameDB + " to " + userDB)
sql.execute("grant create on database " + nameDB + " to " + userDB)

// --------------- Подключение за созданного пользователя на созданную БД ---------------
sql = Sql.newInstance("jdbc:postgresql:" + addrDB + nameDB, userDB, passDB, "org.postgresql.Driver")

// Создается пустая data с таблицами
def schema = "data"
def pgScript = "drop schema if exists " + schema + " cascade;\n"
pgScript += "create schema " + schema + " authorization " + userDB + ";\n"
pgScript += "grant all on schema " + schema + " to " + userDB + ";\n"

// Выполняется создание таблиц и заполнение начальных данных
[
        "/src/install/create_data.sql",
        "/src/install/create_news.sql",
        "/src/install/logging_settings_data.sql",
        "/src/install/create_user_messages.sql",
        "/src/install/fill_initial_data.sql",
        "/src/install/create_documents.sql",
        "/src/install/test_data.sql"
].each {
    new File((String) sourceDir + it).eachLine {
        pgScript += it + "\n"
    }
}

sql.execute(pgScript)