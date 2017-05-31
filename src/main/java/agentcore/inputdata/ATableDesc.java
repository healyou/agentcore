package agentcore.inputdata;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created on 17.02.2017 14:13
 * Данные о структуре таблицы для входных данных агента
 *
 * @autor Nikita Gorodilov
 */
public abstract class ATableDesc {

    /**
     * Колонка id должна быть именно такой
     * тк надо получить доступ к уникальному ключу таблицы
     */
    public final static String ID_COLUMN_NAME = "id";

    /**
     * Тип данных колонки id должнен быть именно такой
     */
    public final static String ID_COLUMN_TYPE = "int";

    protected String tableName;
    protected ImmutableList<TableColumn> columns;

    /**
     * @param tableName имя таблицы
     * @param columns имена колонок и типы данных
     */
    public ATableDesc(String tableName, ImmutableList<TableColumn> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    @Override
    public abstract String toString();

    public String getTableName() {
        return tableName;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public static String translateToSqlType(String type) {
        // todo переписать работу с типами данных(на константы)
        if (type.equals("int"))
            return "INTEGER";
        if (type.equals("String"))
            return "TEXT";
        if (type.equals("double"))
            return "DOUBLE";

        return null;
    }

}
