package agentcore.database.dto;

import com.google.common.base.MoreObjects.ToStringHelper;

import java.util.*;

import static com.google.common.base.MoreObjects.toStringHelper;

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
        ToStringHelper stringHelper = toStringHelper(this);
        for (String columnName : getColumnNames())
            stringHelper.add(columnName, getValueByColumnName(columnName).toString());

        return stringHelper.toString();
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
