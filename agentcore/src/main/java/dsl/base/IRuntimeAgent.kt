package dsl.base

import dsl.objects.DslMessage
import java.awt.Image

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun onLoadImage(image: Image)

    fun onGetMessage(message: DslMessage)

    fun onEndImageTask(updateImage: Image)
}