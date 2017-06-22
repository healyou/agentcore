import groovy.sql.Sql

// --------------- Подключение за пользователя ---------------
sql = Sql.newInstance("jdbc:postgresql:" + addrDB + nameDB, userDB, passDB, "org.postgresql.Driver")


// загрузка новостей
ps = sql.getConnection().prepareStatement("insert into data.news (user_id, headline, text, create_date, image) values (?, ?, ?, now(), ?);")
[
        ["/src/install/images/img1.jpg", "Заголовок новости #1", "Текст новости #1"],
        ["/src/install/images/img2.jpg", "Заголовок новости #2", "Текст новости #2"],
        ["/src/install/images/img3.jpg", "Заголовок новости #3", "Текст новости #3"],
        ["/src/install/images/img1.jpg", "Заголовок новости #4", "Текст новости #4"],
        ["/src/install/images/img2.jpg", "Заголовок новости #5", "Текст новости #5"]
].each {
    ps.setInt(1, 1)
    ps.setString(2, it[1])
    ps.setString(3, it[2])
    ps.setBytes(4, (new File((String) sourceDir + it[0])).getBytes())
    ps.executeUpdate()
}
ps.close()

// Загрузка более сложных сущностей из CSV.
// Пока что парсим исключительно простые поля и поля с одной точкой.
private loadEntityFromCsv(String fileName, String tableName) {

    def lines = parseCsv(new FileReader("" + sourceDir + fileName), separator: ',')

    def columns = []
    lines.columns.findAll() { it.getKey() != "" }.each { it ->
        def splitted = it.getKey().split('\\.')
        def realTable = null
        def columnName = null
        if (splitted.size() == 2) {
            if (splitted[0].contains("(")) {
                realTable = splitted[0].substring(splitted[0].indexOf("(") + 1, splitted[0].length() - 1)
                columnName = splitted[0].substring(0, splitted[0].indexOf("(")) + "_id"
            } else {
                realTable = splitted[0]
                columnName = splitted[0] + "_id"
            }
        }
        def column = ["index"                : it.getValue(),
                      "key"                  : it.getKey(),
                      "simpleField"          : it.getKey().contains(".") ? null : it.getKey(),
                      "dictionaryColumn"     : it.getKey().contains(".") ? columnName : null,
                      "dictionarySourceTable": it.getKey().contains(".") ? realTable : null,
                      "dictionarySourceField": it.getKey().contains(".") ? splitted[1] : null
        ]
        columns.add(column)
    }

    for (line in lines) {
        def insertColumns = []
        def insertValues = []

        def fields = line.toMap()
        for (field in fields) {
            def col = columns.find {it.key == field.getKey()}
            if (col.simpleField != null) {

                insertColumns.add(col.simpleField)
                // тут надо понять что за тип. Сначала выделяем даты
                def value = field.getValue()
                if (value == "") {
                    value = null
                }
                try {
                    def newdate = new Date().parse("dd.MM.yyyy", value)
                    insertValues.add(newdate.toTimestamp())
                    println "Date parsed"
                } catch (ex) {
                    insertValues.add(value)
                }
            }
            if (col.dictionaryColumn != null) {
                insertColumns.add(col.dictionaryColumn)
                def result = sql.firstRow("select id from data."+col.dictionarySourceTable+" where "+col.dictionarySourceField+" = ?", field.getValue())
                insertValues.add(result != null ? result.id : null)
            }
        }
        def marks = []
        (1..insertValues.size()).each {
            marks.add("?")
        }

        // формируем инсерт
        sql.executeInsert("insert into data." + tableName + " ("+ insertColumns.join(',')+") values ("+marks.join(', ')+")", insertValues)
    }

}

//пример использования loadEntityFromCsv
//loadEntityFromCsv("/src/install/experts.csv", "expert")

//lines = parseCsv(new FileReader("" + sourceDir + "/src/install/diagnosis_F.csv"), separator: ',')
////println lines.columns // Список колонок (1-я строка) из CSV файла для дальнейшего использования
//ps = sql.getConnection().prepareStatement("insert into data.diagnosis (code, name) values (?, ?)")
//for (line in lines) {
//    ps.setString(1, "$line.CODE")
//    ps.setString(2, "$line.NAME")
//    ps.execute()
//}
//ps.close();