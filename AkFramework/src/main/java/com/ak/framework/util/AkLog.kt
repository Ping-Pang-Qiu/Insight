package com.ak.framework.util

import android.util.Log
import com.ak.framework.util.AkLog.ENABLE_THREAD_INFO as ENABLE_THREAD_INFO1

object AkLog {
    const val VERBOSE = Log.VERBOSE;
    const val DEBUG = Log.DEBUG;
    const val INFO = Log.INFO;
    const val WARN = Log.WARN;
    const val ERROR = Log.ERROR;
    const val ASSERT = Log.ASSERT;

    private const val TAG = "AkLog"
    private const val ENABLE_THREAD_INFO = false
    private var mDebugEnable = false
    private val mOnceTag: ThreadLocal<String> = ThreadLocal()
    private var mPriority: Int = Log.VERBOSE

    fun config(enable: Boolean, priority: Int) {
        mDebugEnable = enable
        mPriority = priority
    }

    fun isDebug(): Boolean {
        return mDebugEnable
    }

    fun t(tag: String): AkLog {
        mOnceTag.set(tag)
        return this;
    }

    fun v(msg: String): AkLog {
        if (isFilter(VERBOSE)) {
            print(
                VERBOSE,
                msg
            )
        }
        return this;
    }

    fun d(msg: String) {
        if (isFilter(DEBUG)) {
            print(
                DEBUG,
                msg
            )
        }
    }

    fun i(msg: String) {
        if (isFilter(INFO)) {
            print(
                INFO,
                msg
            )
        }
    }

    fun w(msg: String) {
        if (isFilter(WARN)) {
            print(
                WARN,
                msg
            )
        }
    }

    fun e(msg: String) {
        if (isFilter(ERROR)) {
            print(
                ERROR,
                msg
            )
        }
    }

    fun e(throwable: Throwable) {
        if (isFilter(ERROR)) {
            print(
                ERROR,
                null,
                throwable
            )
        }
    }

    fun e(throwable: Throwable, msg: String) {
        if (isFilter(ERROR)) {
            print(
                ERROR,
                msg,
                throwable
            )
        }
    }

    private fun isFilter(priority: Int): Boolean {
        return mDebugEnable && priority >= mPriority
    }

    private fun print(priority: Int, msg: String?, throwable: Throwable? = null) {
        val content = formatMessage(msg, throwable)
        Log.println(priority, TAG, content)
    }

    private fun formatMessage(msg: String?, throwable: Throwable? = null): String {
        var onceTag = mOnceTag.get()
        if (onceTag != null) {
            mOnceTag.remove()
        }

        val stringBuilder = StringBuilder()
        // tag
        if (onceTag != null) {
            stringBuilder.append(onceTag)
        }

        // 线程
        if (ENABLE_THREAD_INFO && stringBuilder.isNotEmpty()) {
            stringBuilder.append(", thread=")
            stringBuilder.append(Thread.currentThread().name)
        }


        // 分割符
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append("|")
        }

        // 消息内容
        if (msg != null) {
            stringBuilder.append(msg)
        }

        // 异常
        if (throwable != null) {
            stringBuilder.append("\n")
            stringBuilder.append(Log.getStackTraceString(throwable))
        }

        return stringBuilder.toString()
    }
}

