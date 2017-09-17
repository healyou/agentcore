package db.base

import org.springframework.jdbc.core.RowMapper
import service.objects.Entity
import java.sql.ResultSet
import java.util.*

/**
 * @author Nikita Gorodilov
 */
abstract class AbstractRowMapper<T: Entity>: RowMapper<T> {

    protected fun getLong(resultSet: ResultSet, columnName: String): Long = resultSet.getLong(columnName)

    protected fun getString(resultSet: ResultSet, columnName: String): String = resultSet.getString(columnName)

    protected fun getDate(resultSet: ResultSet, columnName: String): Date {
        return resultSet.getString(columnName).fromSqlite()
    }

    // todo исправить?
    protected fun getNullDate(resultSet: ResultSet, columnName: String): Date? {
        return resultSet.getString(columnName)?.fromSqlite()
    }
}