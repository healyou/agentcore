package com.company

import java.awt.Image

/**
 * Функции для работы с изображениями
 *
 * @author Nikita Gorodilov
 */
object AgentImageFunctions {

    @JvmStatic
    fun testImageFun1() {
        println("testImageFun1")
    }

    @JvmStatic
    fun testImageFun2() {
        println("testImageFun2")
    }

    @JvmStatic
    fun testImageFun3() {
        println("testImageFun3")
    }

    @JvmStatic
    fun testUpdateImageWithSleep(imageData: ByteArray, sleep: Long = 5000): ByteArray {
        println("start testUpdateImageWithSleep")
        Thread.sleep(sleep)
        println("end testUpdateImageWithSleep")
        return imageData
    }
}