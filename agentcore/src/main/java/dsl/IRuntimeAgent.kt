package dsl

import db.core.servicemessage.ServiceMessage
import java.awt.Image

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun onLoadImage(image: Image)

    fun onGetMessage(serviceMessage: ServiceMessage)

    fun onEndImageTask(updateImage: Image)
}