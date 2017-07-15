package agentcore.database.dto

import java.io.Serializable

import com.google.common.base.Objects

/**
 * Класс объекта бд о таблице которого
 * мы узнаем в runtime
 *
 * @author Nikita Gorodilov
 */
abstract class ConfigureEntity : Serializable {

    /**
     * @return типы данных столбцов
     */
    abstract fun getColumnTypes(): Collection<String>

    /**
     * @return имена столбцов
     */
    abstract fun getColumnNames(): Set<String>

    /**
     * @return значения столбцов
     */
    abstract fun getColumnValues(): Set<String>

    /**
     * @return тип данных столбца по его имени
     */
    abstract fun getTypeByColumnName(columnName: String): String?

    /**
     * @return значение столбца по его имени
     */
    abstract fun getValueByColumnName(columnName: String): Any?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as ConfigureEntity

        return Objects.equal(getColumnNames(), that.getColumnNames())
                && Objects.equal(getColumnValues(), that.getColumnValues())
                && Objects.equal(getColumnTypes(), that.getColumnTypes())
    }

    override fun hashCode(): Int {
        var result = super.hashCode()

        getColumnNames()
                .asSequence()
                .map { getValueByColumnName(it) }
                .forEach { result = 31 * result + (it?.hashCode() ?: 0) }

        return result
    }

    abstract override fun toString(): String
}