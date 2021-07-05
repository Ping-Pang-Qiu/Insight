package com.ak.framework.task

import com.ak.framework.developer.AkDevelopException
import com.ak.framework.developer.AkDeveloper
import com.ak.framework.util.AkLog
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class AkThreadPoolExecutor : ThreadPoolExecutor {
    private companion object {
        private const val TAG = "AkThreadPoolExecutor"
    }

    private val mName: String

    constructor(
        name: String,
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
    ) : this(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, AbortPolicy())

    constructor(
        name: String,
        corePoolSize: Int,
        maximumPoolSize: Int,
        keepAliveTime: Long,
        unit: TimeUnit,
        workQueue: BlockingQueue<Runnable>,
        handler: RejectedExecutionHandler
    ) : super(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        InnerThreadFactory(name),
        InnerRejectedExecutionHandler(name, handler)
    ) {
        mName = name
    }

    override fun execute(runnable: Runnable) {
        if (AkDeveloper.isEnable()) {
            if (runnable is AkRunnable) {
                runnable.onPostExecute()
            } else {
                throw AkDevelopException("$mName, not use AkRunnable")
            }
        }

        super.execute(runnable)
    }


    private class InnerThreadFactory(name: String) : ThreadFactory {
        private val mGroup: ThreadGroup
        private val mThreadNumber = AtomicInteger(1)
        private val mNamePrefix: String


        init {
            val s = System.getSecurityManager()
            mGroup = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            mNamePrefix = "$name-pool-"
        }

        override fun newThread(r: Runnable): Thread {
            val t = AkThread(
                mNamePrefix + mThreadNumber.getAndIncrement(),
                r, mGroup, 0
            )
            if (t.isDaemon) t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
            return t
        }
    }

    private class InnerRejectedExecutionHandler(
        private val mName: String,
        private val mHandler: RejectedExecutionHandler
    ) : RejectedExecutionHandler {
        override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
            if (AkDeveloper.isEnable()) {
                if (r is AkRunnable) {
                    AkLog.t(TAG).w("$mName,reject task ${r.getName()}")
                } else {
                    throw AkDevelopException("$mName, not use AkRunnable")
                }
            }

            mHandler.rejectedExecution(r, executor)
        }
    }
}