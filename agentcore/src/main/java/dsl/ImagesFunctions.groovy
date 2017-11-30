package dsl

import com.company.AgentImageFunctions
import dsl.objects.DslImage

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

    static void testUpdateImageWithSleep(Object self, DslImage dslImage, Long sleep) {
        def runtimeAgent = (self.delegate as RuntimeAgentService).runtimeAgent
        def updateImageData = AgentImageFunctions.testUpdateImageWithSleep(dslImage.data, sleep)
        dslImage.data = updateImageData
        runtimeAgent.onEndImageTask(dslImage)
    }
}
