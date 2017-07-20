package agentcore.database.dto;

import agentcore.utils.Codable;

/**
 * Типы данных
 *
 * @author Nikita Gorodilov
 */
public enum InputDataType implements Codable<String> {
    STRING("String"),
    INT("int"),
    DOUBLE("double");

    private String code;

    InputDataType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
