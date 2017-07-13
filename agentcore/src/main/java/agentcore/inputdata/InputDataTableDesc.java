package agentcore.inputdata;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author Nikita Gorodilov
 */
public class InputDataTableDesc extends ATableDesc {

    private String agentID;
    private Integer periodicityMS;
    private String outputType;
    private Pattern comRegExp;

    /**
     * @param tableName имя таблицы
     * @param periodicityMS периодичность просмотра таблицы(берём 1 запись и удаляем её)
     * @param columns имена колонок и типы данных
     * @param outputType тип данных выходного значения
     * @param comRegExp проверка, надо ли делать общее решение
     */
    public InputDataTableDesc(@NotNull String tableName,
                              @NotNull Integer periodicityMS,
                              @NotNull ImmutableList<TableColumn> columns,
                              @NotNull String outputType,
                              @NotNull Pattern comRegExp,
                              @NotNull String agentID) {
        super(tableName, columns);

        this.periodicityMS = periodicityMS;
        this.outputType = outputType;
        this.comRegExp = comRegExp;
        this.agentID = agentID;
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

    @NotNull
    public String getOutputType() {
        return outputType;
    }

    @NotNull
    public Pattern getComRegExp() {
        return comRegExp;
    }

    @NotNull
    public Integer getPeriodicityMS() {
        return periodicityMS;
    }

    @NotNull
    public String getAgentID() { return agentID; }
}
