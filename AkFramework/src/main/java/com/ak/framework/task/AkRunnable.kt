package com.ak.framework.task

import android.os.Looper
import androidx.annotation.CallSuper
import com.ak.framework.developer.AkDeveloper
import com.ak.framework.util.AkLog

abstract class AkRunnable(private val mName: String) : Runnable {
    private companion object {
        private const val TAG = "AkRunnable"
    }

    private var mTaskWatchDog: TaskWatchDog? = null

    fun getName(): String {
        return mName
    }

    override fun run() {
        var oldName: String? = null
        if (Looper.getMainLooper() != Looper.myLooper()) {
            oldName = Thread.currentThread().name
            Thread.currentThread().name = "$oldName: $mName"
        }

        onExecuteStart()
        try {
            execute()
        } catch (e: Exception) {
            AkLog.t(TAG).e(e, "$mName, occur error")
            onExecuteError()
        } finally {
            onExecuteEnd()
            if (oldName != null) {
                Thread.currentThread().name = oldName
            }
        }
    }

    fun setupWatchDog(waitTimeout: Long, executeTimeout: Long) {
        if (AkDeveloper.isEnable() && (waitTimeout > 0 || executeTimeout > 0)) {
            mTaskWatchDog = TaskWatchDog(mName).apply {
                setupWatchDog(waitTimeout, executeTimeout)
            }
        }
    }

    @CallSuper
    fun onPostExecute() {
        mTaskWatchDog?.onPostExecute()
    }

    @CallSuper
    fun onExecuteStart() {
        mTaskWatchDog?.onExecuteStart()
    }

    @CallSuper
    fun onExecuteError() {
    }

    @CallSuper
    fun onExecuteEnd() {
        mTaskWatchDog?.onExecuteEnd()
    }

    abstract fun execute()
}