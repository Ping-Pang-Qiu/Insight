package com.ak.framework.task

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.ak.framework.developer.AkDevelopException
import com.ak.framework.developer.AkDeveloper
import com.ak.framework.util.AkLog

class AkHandler : Handler {
    private companion object {
        private const val TAG = "AkHandler"

        private const val DEBUG_MAIN_HANDLER_DISPATCH_TIMEOUT = 50L
        private const val DEBUG_MAIN_HANDLER_EXECUTE_TIMEOUT = 30L
    }

    private val mName: String

    constructor(name: String, looper: Looper) : this(name, looper, null)

    constructor(name: String, looper: Looper, callback: Callback?) : super(looper, callback) {
        mName = name
    }

    override fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
        if (AkDeveloper.isEnable()) {
            val task = msg.callback
            if (task != null && task !is AkRunnable) {
                if (task is AkRunnable) {
                    task.onPostExecute()
                } else {
                    throw AkDevelopException("$mName, not use AkRunnable")
                }
            }
        }
        return super.sendMessageAtTime(msg, uptimeMillis)
    }

    override fun dispatchMessage(msg: Message) {
        val debugAnr = AkDeveloper.isEnable() && looper == Looper.getMainLooper()
        if (debugAnr) {
            var dispatchTime: Long = SystemClock.uptimeMillis()
            val task = msg.callback
            val content = if (task != null) {
                if (task is AkRunnable) "task: ${task.getName()}" else "task: not use AkRunnable"
            } else {
                "msg: what=${msg.what}"
            }

            // 执行时间，判断是否延迟过长
            if (dispatchTime - msg.`when` > DEBUG_MAIN_HANDLER_DISPATCH_TIMEOUT) {
                AkLog.t(TAG)
                    .w("$mName, main handler dispatch timeout $DEBUG_MAIN_HANDLER_DISPATCH_TIMEOUT, $content")
            }

            // 执行时长，判断是否执行耗时过长
            val result = super.dispatchMessage(msg)
            if (SystemClock.uptimeMillis() - dispatchTime > DEBUG_MAIN_HANDLER_EXECUTE_TIMEOUT) {
                AkLog.t(TAG)
                    .w("$mName, main handler execute timeout $DEBUG_MAIN_HANDLER_EXECUTE_TIMEOUT, $content")
            }

            return result
        } else {
            return super.dispatchMessage(msg)
        }
    }
}