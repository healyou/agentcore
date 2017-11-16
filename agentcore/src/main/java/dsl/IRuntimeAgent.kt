package dsl

import db.core.servicemessage.ServiceMessage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    fun onLoadImage()

    fun onGetMessage(serviceMessage: ServiceMessage)

    fun onEndTask()
}