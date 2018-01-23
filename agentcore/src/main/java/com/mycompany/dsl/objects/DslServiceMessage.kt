package com.mycompany.dsl.objects

/**
 * Класс сервисного сообщения(свзяь между агентами), который используется в DSL(для лёгкости использования)
 *
 * @author Nikita Gorodilov
 */
open class DslServiceMessage(
        /* Тип отправителя сообщения - AgentType.Code.code */
        val senderType: String,
        /* Изображение */
        val image: DslImage
)