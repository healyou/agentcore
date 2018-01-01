package com.mycompany.dsl.objects

/**
 * Класс сообщения, который используется в DSL(для лёгкости использования)
 *
 * @author Nikita Gorodilov
 */
open class DslMessage(
        /* Тип отправителя сообщения - AgentType.Code.code */
        val senderType: String,
        /* Изображение */
        val image: DslImage
)