package inputdata.inputdataverification.inputdata;

import com.google.common.collect.ImmutableList;
import inputdata.inputdataverification.inputdata.base.ATableDesc;

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
        sb.append("[tableName = " + tableName + ", columns = \n");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sb.append('(' + tableColumn.getColumnName() + " - ");
            sb.append(tableColumn.getColumnType() + ")\n");
        }

        sb.append("]");
        return sb.toString();
    }

}

