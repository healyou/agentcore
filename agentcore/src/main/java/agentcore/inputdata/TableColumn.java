package agentcore.inputdata;

/**
 * Created on 17.02.2017 14:20
 * Хранит имя столбца таблицы бд и тип данных
 *
 * @autor Nikita Gorodilov
 */
public class TableColumn {

    private String columnName;
    private String columnType;

    public TableColumn(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

}
