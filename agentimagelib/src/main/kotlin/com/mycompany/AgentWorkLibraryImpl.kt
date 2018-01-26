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
            onAgentEvent("testImageFun1")
            val k = 1
        }

        @JvmStatic
        fun testImageFun2(self: Any, value: Array<Any>) {
            println("testImageFun2")
            onAgentEvent("testImageFun2")
            val k = 1
        }

        @JvmStatic
        fun testImageFun3(self: Any, value: Array<Any>) {
            println("testImageFun3")
        }

        @JvmStatic
        fun testUpdateImageWithSleep(imageData: ByteArray, sleep: Long = 5000): ByteArray {
            Thread.sleep(sleep)
            return imageData
        }

        private fun onAgentEvent(event: String) {
            AAgentWorkLibrary.getAgentWorkControl().onAgentEvent(1, event)
        }

        /**
         * Функции для интеграционного тестирования агента
         */
        @JvmStatic
        fun testLibOnAgentStartA1(self: Any, value: Array<Any>) {
            onAgentEvent("local_event_a1")
        }

        @JvmStatic
        fun testLibOnStartTaskA1(self: Any, value: Array<Any>) {
            /* nothing */
        }

        @JvmStatic
        fun testLibOnGetServiceMessageA2(self: Any, value: Array<Any>) {
            onAgentEvent("local_event_a2")
        }
    }
}