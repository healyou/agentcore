package dsl

/**
 * Параметры метода sendMessage в dsl
 *
 * @author Nikita Gorodilov
 */
enum class SendMessageParameters(val paramName: String, val isRequired: Boolean) {
    /**
     * Тип сообщения
     */
    MESSAGE_TYPE("messageType", true),
    /**
     * Изображение
     */
    IMAGE("image", true),
    /**
     * Тип агентов, которым надо отправлять сообщение
     */
    AGENT_TYPES("agentTypes", true),
    /**
     * Тип тела сообщения - default JSON
     */
    BODY_TYPE("bodyType", false),
    /**
     * Цель общения - default работа с изображением
     */
    MESSAGE_GOAL_TYPE("messageGoalType", false);
}