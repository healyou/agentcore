package inputdata.inputdataverification.inputdata;

import com.google.common.collect.ImmutableList;

import java.util.regex.Pattern;

/**
 * Created by user on 21.02.2017.
 */
public class InputDataTableDesc extends ATableDesc {

    private int periodicityMS;
    private String outputType;
    private Pattern comRegExp;

    /**
     * @param tableName имя таблицы
     * @param periodicityMS периодичность просмотра таблицы(берём 1 запись и удаляем её)
     * @param columns имена колонок и типы данных
     * @param outputType тип данных выходного значения
     * @param comRegExp проверка, надо ли делать общее решение
     */
    public InputDataTableDesc(String tableName, int periodicityMS, ImmutableList<TableColumn> columns,
                              String outputType, Pattern comRegExp) {
        super(tableName, columns);

        this.periodicityMS = periodicityMS;
        this.outputType = outputType;
        this.comRegExp = comRegExp;
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

    public String getOutputType() {
        return outputType;
    }

    public Pattern getComRegExp() {
        return comRegExp;
    }

    public int getPeriodicityMS() {
        return this.periodicityMS;
    }

}
