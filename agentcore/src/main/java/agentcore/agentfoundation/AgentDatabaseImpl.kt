package agentcore.agentfoundation

import com.google.common.collect.ImmutableList
import agentcore.database.dao.LocalDataDao
import agentcore.database.dto.LocalDataDto
import agentcore.inputdata.InputDataTableDesc
import agentcore.inputdata.LocalDataTableDesc
import agentcore.inputdata.ATableDesc
import agentcore.inputdata.TableColumn
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.net.URL
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList
import java.util.Observable
import java.util.Properties

import com.google.common.base.Preconditions.checkNotNull

/**
 * @author Nikita Gorodilov
 */
class AgentDatabaseImpl(inputDataTD: InputDataTableDesc, localdbResPath: String) : Observable(), IAgentDatabase {

    override val localDbTableDesc: LocalDataTableDesc
        get

    private val jdbcTemplate: JdbcTemplate
    private val localDataDao: LocalDataDao

    private val DB_PROPERTIES_PATH: String = localdbResPath

    init {
        jdbcTemplate = getJdbcTemplate()
        try {
            clearDatabase()
        } catch (e: SQLException) {
            throw RuntimeException("Невозможно очистить локальную базу данных")
        }

        createOrOpenDatabase(jdbcTemplate, inputDataTD)
        localDbTableDesc = createLocalDbDesc(inputDataTD)
        localDataDao = LocalDataDao(jdbcTemplate, localDbTableDesc)/*как сделать dao для 2 бд*/
    }

    @Throws(SQLException::class)
    override fun addSolution(dtoEntity: LocalDataDto) {
        localDataDao.create(dtoEntity)
    }

    @Throws(SQLException::class)
    override fun updateSolution(dtoEntity: LocalDataDto) {
        localDataDao.update(dtoEntity)
    }

    @Throws(SQLException::class)
    override fun clearDatabase() {
        val statmt = jdbcTemplate.dataSource.connection.createStatement()
        statmt.execute("drop table if exists $TABLE_NAME;")
    }

    /**
     * Создаём таблицы в локальной бд, если их ещё нет
     * @param jdbcTemplate работа с бд
     * *
     * @param inputDataTD структура таблицы входных данных
     */
    private fun createOrOpenDatabase(jdbcTemplate: JdbcTemplate, inputDataTD: ATableDesc) {
        try {
            val statement = jdbcTemplate.dataSource.connection.createStatement()

            // создание таблицы в локальной бд
            statement.execute(createSqlQuery(inputDataTD))
        } catch (e: SQLException) {
            println(e.toString())
            System.exit(2)
        }

    }

    private fun createSqlQuery(inputDataTD: ATableDesc): String {
        val sql = StringBuilder()
        sql.append("CREATE TABLE if not exists " + TABLE_NAME)
        sql.append("    (id INTEGER PRIMARY KEY NOT NULL,")

        for (tableColumn in inputDataTD.columns) {
            if (tableColumn.columnName == InputDataTableDesc.ID_COLUMN_NAME)
                continue

            val columnName = tableColumn.columnName
            val columnType = ATableDesc.translateToSqlType(tableColumn.columnType)
            checkNotNull(columnName, "columnName is not null")
            checkNotNull(columnType, "columnType is not null")

            val temp = "$columnName $columnType,"
            sql.append(temp)
        }

        sql.append(ANSWER_COLUMN_NAME + " TEXT,")
        sql.append(COLLECTIVEANSWER_COLUMN_NAME + " TEXT);")

        return sql.toString()
    }

    private fun createLocalDbDesc(inputDataTD: ATableDesc): LocalDataTableDesc {
        val columns = ArrayList<TableColumn>()

        for (tableColumn in inputDataTD.columns)
            columns.add(TableColumn(tableColumn.columnName, tableColumn.columnType))

        columns.add(TableColumn(ANSWER_COLUMN_NAME, "String"))
        columns.add(TableColumn(COLLECTIVEANSWER_COLUMN_NAME, "String"))

        return LocalDataTableDesc(TABLE_NAME, ImmutableList.copyOf(columns))
    }

    private fun getJdbcTemplate(): JdbcTemplate {
        val ds = DriverManagerDataSource()

        val dbprop = Properties()
        try {
            val resourceUrl = javaClass.classLoader.getResource(DB_PROPERTIES_PATH)
            checkNotNull(resourceUrl)

            dbprop.load(resourceUrl!!.openStream())

            val driverClassName = dbprop.getProperty("driverClassName")
            val url = dbprop.getProperty("url")

            ds.setDriverClassName(driverClassName)
            ds.url = url
        } catch (e: Exception) {
            println(e.toString())
            System.exit(1)
        }

        return JdbcTemplate(ds)
    }

    companion object {

        // TEXT - String type for table value
        val ANSWER_COLUMN_NAME = "answer"
        val COLLECTIVEANSWER_COLUMN_NAME = "collectiveanswer"
        val TABLE_NAME = "localdata"
    }
}
