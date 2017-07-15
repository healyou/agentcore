package agentcore.database.dto;

import com.google.common.base.MoreObjects.ToStringHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.google.common.base.MoreObjects.toStringHelper;

// todo выделить новый объект для передачи по сети
// todo сделать метод getId у основных объектов системы

/**
 * @author Nikita Gorodilov
 */
public class ConfigureEntityImpl extends ConfigureEntity {

    protected HashMap<String, Object> paramValue;
    protected HashMap<String, String> paramType;

    public ConfigureEntityImpl(@NotNull HashMap<String, String> paramType,
                               @NotNull HashMap<String, Object> paramValue) {
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

    @NotNull
    @Override
    public String toString() {
        ToStringHelper stringHelper = toStringHelper(this);
        for (String columnName : getColumnNames()) {
            Object value = getValueByColumnName(columnName);
            stringHelper.add(columnName, value == null ? "null" : value.toString());
        }

        return stringHelper.toString();
    }

    @NotNull
    @Override
    public Collection<String> getColumnTypes() {
        return paramType.values();
    }

    @NotNull
    @Override
    public Set<String> getColumnNames() {
        return paramType.keySet();
    }

    @NotNull
    @Override
    public Set<String> getColumnValues() {
        return paramValue.keySet();
    }

    @Override
    public String getTypeByColumnName(@NotNull String columnName) {
        return paramType.get(columnName);
    }

    @Override
    public Object getValueByColumnName(@NotNull String columnName) {
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
