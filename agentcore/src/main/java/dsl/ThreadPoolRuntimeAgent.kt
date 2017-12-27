package dsl

import db.core.file.dslfile.DslFileAttachment
import dsl.objects.DslImage
import dsl.objects.DslMessage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Оборачивает вызовы основных функций агента в threadpool функции
 *
 * @author Nikita Gorodilov
 */
abstract class ThreadPoolRuntimeAgent(dslFileAttachment: DslFileAttachment) : RuntimeAgent(dslFileAttachment) {

    companion object {
        var executorService: ExecutorService = createExecutors()

        fun createExecutors(): ExecutorService {
            return Executors.newFixedThreadPool(4, { r ->
                val t = Executors.defaultThreadFactory().newThread(r)
                t.isDaemon = true
                t
            })
        }
    }

    override fun onGetMessage(message: DslMessage) {
        executorService.execute {
            super.onGetMessage(message)
        }
    }

    override fun onLoadImage(image: DslImage) {
        executorService.execute {
            super.onLoadImage(image)
        }
    }

    override fun onEndImageTask(updateImage: DslImage) {
        executorService.execute {
            super.onEndImageTask(updateImage)
        }
    }
}
