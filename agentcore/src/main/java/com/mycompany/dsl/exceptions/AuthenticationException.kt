package com.mycompany.dsl.exceptions

/**
 * Ошибки авторизации пользователя
 *
 * @author Nikita Gorodilov
 */
class AuthenticationException(message: String): RuntimeException(message)