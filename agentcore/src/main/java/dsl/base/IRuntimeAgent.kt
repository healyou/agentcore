package dsl.base

import dsl.objects.DslImage
import dsl.objects.DslMessage

/**
 * @author Nikita Gorodilov
 */
interface IRuntimeAgent {

    // TODO на основные операции threadpool и норм для всех агентов 1 и тот же
    fun onLoadImage(image: DslImage)

    fun onGetMessage(message: DslMessage)

    fun onEndImageTask(updateImage: DslImage)
}