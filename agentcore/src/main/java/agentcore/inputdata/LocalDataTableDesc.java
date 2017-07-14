package agentcore.inputdata;

import com.google.common.collect.ImmutableList;

/**
 * Created by user on 21.02.2017.
 */
public class LocalDataTableDesc extends ATableDesc {

    /**
     * @param tableName имя таблицы
     * @param columns   имена колонок и типы данных
     */
    public LocalDataTableDesc(String tableName, ImmutableList<TableColumn> columns) {
        super(tableName, columns);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[tableName = ").append(tableName).append(", columns = \n");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sb.append('(').append(tableColumn.getColumnName()).append(" - ");
            sb.append(tableColumn.getColumnType()).append(")\n");
        }

        sb.append("]");
        return sb.toString();
    }

}

