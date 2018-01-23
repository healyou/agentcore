package com.mycompany.dsl.base

import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun start()

    fun stop()

    fun onLoadImage(image: DslImage)

    fun onGetServiceMessage(serviceMessage: DslServiceMessage)

    fun onGetLocalMessage(localMessage: DslLocalMessage)

    fun onEndImageTask(updateImage: DslImage)
}