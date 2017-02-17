package inputdata.database.dto;

import inputdata.inputdataverification.inputdata.InputTableDesc;
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

    private InputTableDesc inputTableDesc;

    public DtoEntityImplRowMapper(InputTableDesc inputTableDesc) {
        this.inputTableDesc = inputTableDesc;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public DtoEntityImpl mapRow(ResultSet rs, int i) throws SQLException {
        HashMap<String, String> paramType = new HashMap<>();
        HashMap<String, Object> paramValue = new HashMap<>();
        List<TableColumn> columns = inputTableDesc.getColumns();

        for (TableColumn column : columns)
            paramType.put(column.getColumnName(), column.getColumnType());

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

        DtoEntityImpl dtoEntity = null;
        if (paramType.size() == paramValue.size())
            dtoEntity = new DtoEntityImpl(paramType, paramValue);

        return dtoEntity;
    }

}
