package com.mycompany.dsl.base.parameters

/**
 * Параметры метода sendServiceMessage в dsl
 *
 * @author Nikita Gorodilov
 */
enum class SendServiceMessageParameters(val paramName: String, val isRequired: Boolean) {
    /**
     * Тип сообщения
     * String
     */
    MESSAGE_TYPE("messageType", true),
    /**
     * Изображение
     * String
     */
    MESSAGE_BODY("messageBody", true),
    /**
     * Тип агентов, которым надо отправлять сообщение
     * List<String>
     */
    AGENT_TYPES("agentTypes", true),
    /**
     * Тип тела сообщения - default json
     * String
     */
    BODY_TYPE("bodyType", false),
}