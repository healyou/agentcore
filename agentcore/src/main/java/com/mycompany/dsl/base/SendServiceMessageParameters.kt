package com.mycompany.dsl.base

/**
 * Параметры метода sendServiceMessage в dsl
 *
 * @author Nikita Gorodilov
 */
enum class SendServiceMessageParameters(val paramName: String, val isRequired: Boolean) {
    /**
     * Тип сообщения
     */
    MESSAGE_TYPE("messageType", true),
    /**
     * Изображение
     */
    MESSAGE_BODY("messageBody", true),
    /**
     * Тип агентов, которым надо отправлять сообщение
     */
    AGENT_TYPES("agentTypes", true),
    /**
     * Тип тела сообщения - default json
     */
    BODY_TYPE("bodyType", false),
}