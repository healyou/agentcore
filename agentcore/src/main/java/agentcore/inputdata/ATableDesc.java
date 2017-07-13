package agentcore.inputdata;

import agentcore.database.dto.InputDataType;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Данные о структуре таблицы для входных данных агента
 *
 * @author Nikita Gorodilov
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
        switch (InputDataType.getByName(type)) {
            case STRING: {
                return "TEXT";
            }
            case INT: {
                return "INTEGER";
            }
            case DOUBLE: {
                return "DOUBLE";
            }
            default: {
                throw new UnsupportedOperationException("Не известный тип данных");
            }
        }
    }
}
