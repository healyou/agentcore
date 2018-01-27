package com.mycompany.dsl.objects

/**
 * Данные о текущем экземпляре агента, доступные в dsl
 *
 * @author Nikita Gorodilov
 */
class DslAgentData(
        /**
         * Идентификатор агента в базе данных
         * {@link SystemAgent#id}
         */
        val id: Long
)