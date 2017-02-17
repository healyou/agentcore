package inputdata.inputdataverification.inputdata;

import com.google.common.collect.ImmutableList;

/**
 * Created on 17.02.2017 14:13
 *
 * @autor Nikita Gorodilov
 */
public class InputTableDesc {

    private String tableName;
    private int periodicityMS;
    private ImmutableList<TableColumn> columns;

    public InputTableDesc(String tableName, int periodicityMS, ImmutableList<TableColumn> columns) {
        this.tableName = tableName;
        this.periodicityMS = periodicityMS;
        this.columns = columns;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[tableName = " + tableName + ", periodicityMS = " + periodicityMS +
                ", columns = \n");

        for (int i = 0; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            sb.append('(' + tableColumn.getColumnName() + " - ");
            sb.append(tableColumn.getColumnType() + ")\n");
        }

        sb.append("]");
        return sb.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public int getPeriodicityMS() {
        return periodicityMS;
    }

    public ImmutableList<TableColumn> getColumns() {
        return columns;
    }

}
