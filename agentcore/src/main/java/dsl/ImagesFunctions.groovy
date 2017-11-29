package dsl

import com.company.AgentImageFunctions

import java.awt.Image

/**
 * Функции для работы с изображениями, которые можно использовать в dsl
 *
 * @author Nikita Gorodilov
 */
class ImagesFunctions {

    static void testImageFun1(Object self, value) {
        AgentImageFunctions.testImageFun1()
    }

    static void testImageFun2(Object self, value) {
        AgentImageFunctions.testImageFun2()
    }

    static void testImageFun3(Object self, value) {
        AgentImageFunctions.testImageFun3()
    }

    static void testUpdateImageWithSleep(Object self, Image image, Long sleep) {
        def runtimeAgent = (self.delegate as RuntimeAgentService).runtimeAgent
        def updateImage = AgentImageFunctions.testUpdateImageWithSleep(image, sleep)
        runtimeAgent.onEndImageTask(updateImage)
    }
}
