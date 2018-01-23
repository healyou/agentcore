package com.mycompany.dsl

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Оборачивает вызовы основных функций агента в threadpool функции
 *
 * @author Nikita Gorodilov
 */
abstract class ThreadPoolRuntimeAgent : RuntimeAgent {
    constructor(serviceLogin: String): super(serviceLogin)
    constructor(dslFileAttachment: DslFileAttachment): super(dslFileAttachment)

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

    override fun onGetServiceMessage(serviceMessage: DslServiceMessage) {
        executorService.execute {
            super.onGetServiceMessage(serviceMessage)
        }
    }

    override fun onGetLocalMessage(localMessage: DslLocalMessage) {
        executorService.execute {
            super.onGetLocalMessage(localMessage)
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
