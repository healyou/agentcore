package com.mycompany.dsl

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.dsl.base.SystemEvent
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import com.mycompany.dsl.objects.DslTaskData
import com.mycompany.user.User
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

    override fun onEndTask(taskData: DslTaskData) {
        executorService.execute {
            super.onEndTask(taskData)
        }
    }

    override fun onGetSystemEvent(systemEvent: SystemEvent) {
        executorService.execute {
            super.onGetSystemEvent(systemEvent)
        }
    }
}
