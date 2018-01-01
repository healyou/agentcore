package com.mycompany.dsl

import com.company.AgentImageFunctions

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

    /**
     * Вызов всех функций с именовынными параметрами идёт как Map(тут все параметры хранятся)
     *
     * @param self
     * @param value
     */
    static void testUpdateImageWithSleep(Object self, value) {
        def image = value["image"]
        def sleep = value["sleep"]

        def runtimeAgent = (self.delegate as RuntimeAgentService).runtimeAgent
        def updateImageData = AgentImageFunctions.testUpdateImageWithSleep(image.data, sleep)
        image.data = updateImageData
        runtimeAgent.onEndImageTask(image)
    }
}
