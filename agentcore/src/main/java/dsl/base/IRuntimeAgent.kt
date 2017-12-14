package dsl.base

import dsl.objects.DslImage
import dsl.objects.DslMessage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {
    
    fun onLoadImage(image: DslImage)

    fun onGetMessage(message: DslMessage)

    fun onEndImageTask(updateImage: DslImage)
}