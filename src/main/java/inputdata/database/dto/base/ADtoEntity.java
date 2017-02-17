package inputdata.database.dto.base;

import inputdata.database.dto.DtoEntityImpl;
import inputdata.database.InputDataUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Created on 17.02.2017 16:25
 *
 * @autor Nikita Gorodilov
 */
public abstract class ADtoEntity implements Serializable {

    public abstract Collection<String> getColumnTypes();
    public abstract Set<String> getColumnNames();
    public abstract Set<String> getColumnValues();
    public abstract String getTypeByColumnName(String columnName);
    public abstract Object getValueByColumnName(String columnName);

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtoEntityImpl that = (DtoEntityImpl) o;

        if (!InputDataUtils.equals(getColumnNames(), that.getColumnNames())) return false;
        if (!InputDataUtils.equals(getColumnValues(), that.getColumnValues())) return false;
        if (!InputDataUtils.equals(getColumnTypes(), that.getColumnTypes())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        for (String columnName : getColumnNames()) {
            Object value = getValueByColumnName(columnName);
            result = 31 * result + (value != null ? value.hashCode() : 0);
        }

        return result;
    }

    @Override
    public abstract String toString();

}
