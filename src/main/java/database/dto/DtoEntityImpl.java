package database.dto;

import java.util.*;

/**
 * Created on 17.02.2017 16:26
 *
 * @autor Nikita Gorodilov
 */
public class DtoEntityImpl extends ABaseDtoEntity {

    protected HashMap<String, Object> paramValue;
    protected HashMap<String, String> paramType;

    public DtoEntityImpl(HashMap<String, String> paramType, HashMap<String, Object> paramValue) {
        super();
        setUpData(paramType, paramValue);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Entity[");

        for (String columnName : getColumnNames()) {
            Object value = getValueByColumnName(columnName);
            sb.append(value.toString());
            sb.append(" ");
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public Collection<String> getColumnTypes() {
        return paramType.values();
    }

    @Override
    public Set<String> getColumnNames() {
        return paramType.keySet();
    }

    @Override
    public Set<String> getColumnValues() {
        return paramValue.keySet();
    }

    @Override
    public String getTypeByColumnName(String columnName) {
        return paramType.get(columnName);
    }

    @Override
    public Object getValueByColumnName(String columnName) {
        return paramValue.get(columnName);
    }

    private void setUpData(HashMap<String, String> paramType, HashMap<String, Object> paramValue) {
        if (paramType != null && paramValue != null) {
            this.paramValue = paramValue;
            this.paramType = paramType;
        }
        else {
            this.paramValue = new HashMap<>();
            this.paramType = new HashMap<>();
        }
    }

}
