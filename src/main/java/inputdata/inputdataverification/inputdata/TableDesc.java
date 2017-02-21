package inputdata.inputdataverification.inputdata;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created on 17.02.2017 14:13
 * Данные о структуре таблицы для входных данных агента
 *
 * @autor Nikita Gorodilov
 */
public class TableDesc {

    /**
     * Колонка id должна быть именно такой
     * тк надо получить доступ к уникальному ключу таблицы
     */
    public final static String ID_COLUMN_NAME = "id";

    /**
     * Тип данных колонки id должнен быть именно такой
     */
    public final static String ID_COLUMN_TYPE = "int";

    private String tableName;
    private int periodicityMS;
    private ImmutableList<TableColumn> columns;

    /**
     * @param tableName имя таблицы
     * @param periodicityMS периодичность просмотра таблицы(берём 1 запись и удаляем её)
     * @param columns имена колонок и типы данных
     */
    public TableDesc(String tableName, int periodicityMS, ImmutableList<TableColumn> columns) {
        this.tableName = tableName;
        this.periodicityMS = periodicityMS;
        this.columns = columns;
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

    public String getTableName() {
        return tableName;
    }

    public int getPeriodicityMS() {
        return periodicityMS;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

}
