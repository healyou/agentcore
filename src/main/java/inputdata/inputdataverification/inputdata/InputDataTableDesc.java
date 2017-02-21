package inputdata.inputdataverification.inputdata;

import com.google.common.collect.ImmutableList;
import inputdata.inputdataverification.inputdata.base.ATableDesc;

/**
 * Created by user on 21.02.2017.
 */
public class InputDataTableDesc extends ATableDesc {

    private int periodicityMS;

    /**
     * @param tableName имя таблицы
     * @param periodicityMS периодичность просмотра таблицы(берём 1 запись и удаляем её)
     * @param columns   имена колонок и типы данных
     */
    public InputDataTableDesc(String tableName, int periodicityMS, ImmutableList<TableColumn> columns) {
        super(tableName, columns);

        this.periodicityMS = periodicityMS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[tableName = " + tableName + ", periodicityMS = " + periodicityMS + ", columns = \n");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sb.append('(' + tableColumn.getColumnName() + " - ");
            sb.append(tableColumn.getColumnType() + ")\n");
        }

        sb.append("]");
        return sb.toString();
    }

    public int getPeriodicityMS() {
        return this.periodicityMS;
    }

}
