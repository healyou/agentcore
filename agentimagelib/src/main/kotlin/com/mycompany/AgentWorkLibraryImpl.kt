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
            val test = AAgentWorkLibrary.getAgentWorkControl().onAgentEvent(1, "testImageFun1")
            val k = 1
        }

        @JvmStatic
        fun testImageFun2(self: Any, value: Array<Any>) {
            println("testImageFun2")
            val test = AAgentWorkLibrary.getAgentWorkControl().onAgentEvent(2, "testImageFun2")
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
    }
}