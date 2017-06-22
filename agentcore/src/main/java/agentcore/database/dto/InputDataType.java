package agentcore.database.dto;

/**
 * @author Nikita Gorodilov
 */
public enum InputDataType {

    STRING("String"), INT("int"), DOUBLE("double");

    InputDataType(String typeName) {
        this.typeName = typeName;
    }

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public static String getClassName() {
        return InputDataType.class.getName();
    }

    public static InputDataType getByName(String typeName) {
        if (typeName.equals(STRING.typeName))
            return STRING;
        else if (typeName.equals(INT.typeName))
            return INT;
        else if (typeName.equals(DOUBLE.typeName))
            return DOUBLE;

        throw new UnsupportedOperationException("Неизвестный тип данных");
    }

}
