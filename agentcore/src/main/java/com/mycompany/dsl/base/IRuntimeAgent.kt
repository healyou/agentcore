package com.mycompany.dsl.base

import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslMessage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun start()

    fun stop()

    fun onLoadImage(image: DslImage)

    fun onGetMessage(message: DslMessage)

    fun onEndImageTask(updateImage: DslImage)
}