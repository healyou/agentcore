package inputdata.database.dto;

import inputdata.inputdataverification.inputdata.TableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 17.02.2017 21:05
 *
 * @autor Nikita Gorodilov
 */
public class DtoEntityImplRowMapper implements RowMapper<DtoEntityImpl> {

    private TableDesc tableDesc;

    /**
     * Класс, необходимый для чтения данных из бд
     * @param tableDesc данные о неизвестной заранее структуре таблицы
     */
    public DtoEntityImplRowMapper(TableDesc tableDesc) {
        this.tableDesc = tableDesc;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public DtoEntityImpl mapRow(ResultSet rs, int i) throws SQLException {
        // данные, необходимые для создания объекта
        HashMap<String, String> paramType = new HashMap<>();
        HashMap<String, Object> paramValue = new HashMap<>();
        // значения столбцов таблицы пользователя
        List<TableColumn> columns = tableDesc.getColumns();

        // получаем тип данных параметров
        for (TableColumn column : columns)
            paramType.put(column.getColumnName(), column.getColumnType());

        // получаем значение считываемой строки таблицы
        for (String key : paramType.keySet()) {
            if (paramType.get(key).equals("int")) {
                String columnName = key;
                paramValue.put(key, rs.getInt(columnName));
            }
            if (paramType.get(key).equals("String")) {
                String columnName = key;
                paramValue.put(key, rs.getString(columnName));
            }
        }

        // создание объекта строки таблицы бд
        DtoEntityImpl dtoEntity = null;
        if (paramType.size() == paramValue.size())
            dtoEntity = new DtoEntityImpl(paramType, paramValue);

        return dtoEntity;
    }

}
