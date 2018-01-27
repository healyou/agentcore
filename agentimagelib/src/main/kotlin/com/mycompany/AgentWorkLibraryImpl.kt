package com.mycompany

import com.mycompany.agentworklibrary.AAgentWorkLibrary

/**
 * @author Nikita Gorodilov
 */
class AgentWorkLibraryImpl {

    companion object {
        @JvmStatic
        fun testImageFun1(self: Any, value: Array<Any>) {
            println("testImageFun1")
        }

        @JvmStatic
        fun testImageFun2(self: Any, value: Array<Any>) {
            println("testImageFun2")
        }

        @JvmStatic
        fun testImageFun3(self: Any, value: Array<Any>) {
            println("testImageFun3")
        }

        private fun onAgentEvent(agentId: Long, event: String) {
            AAgentWorkLibrary.getAgentWorkControl().onAgentEvent(agentId, event)
        }

        /**
         * Функции для интеграционного тестирования агента
         */
        @JvmStatic
        fun testLibOnAgentStartA1(self: Any, value: Array<Any>) {
            onAgentEvent(getAgentId(value), "local_event_a1")
        }

        @JvmStatic
        fun testLibOnStartTaskA1(self: Any, value: Array<Any>) {
            /* nothing */
        }

        @JvmStatic
        fun testLibOnGetServiceMessageA2(self: Any, value: Array<Any>) {
            onAgentEvent(getAgentId(value), "local_event_a2")
        }

        private fun getAgentId(value: Array<Any>): Long {
            if (value.size == 1 && value[0] is Long) {
                return value[0] as Long
            } else {
                throw RuntimeException("Не передан идентификатор агента в функцию библиотеки")
            }
        }
    }
}