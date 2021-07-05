package com.ak.framework.task


import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import com.ak.framework.util.AkLog
import com.ak.framework.util.AkToast

internal class TaskWatchDog(
    private val mName: String
) {
    private companion object {
        private const val TAG = "TaskWatchDog"

        private const val MSG_CHECK_WAIT = 0x1
        private const val MSG_CHECK_WORK = 0x2

        private val mWatchHandlerThread: HandlerThread = HandlerThread(TAG)
        private val mWatchLooper: Looper

        init {
            mWatchHandlerThread.start()
            mWatchLooper = mWatchHandlerThread.looper
        }
    }

    // 任务等待时长警告阈值
    private var mWaitTimeoutMillis: Long = 0

    // 任务执行时长警告阈值
    private var mExecuteTimeoutMillis: Long = 0

    private val mWatchHandler: Handler
    private var mPostTime: Long = 0 //任务投递时间
    private var mStartTime: Long = 0 //开始执行时间

    init {
        mWatchHandler = AkHandler(
            TAG,
            mWatchLooper, Handler.Callback { msg ->
                if (msg.what == MSG_CHECK_WAIT) {
                    val msg = "$mName, wait timeout $mWaitTimeoutMillis"
                    AkLog.t(TAG).w(msg)
                    AkToast.show(msg)
                } else if (msg.what == MSG_CHECK_WORK) {
                    val msg = "$mName, execute timeout $mExecuteTimeoutMillis"
                    AkLog.t(TAG).w(msg)
                    AkToast.show(msg)
                }
                true
            })
    }

    fun setupWatchDog(waitTimeout: Long, executeTimeout: Long) {
        mWaitTimeoutMillis = waitTimeout
        mExecuteTimeoutMillis = executeTimeout
    }

    /**
     * 任务投递
     */
    fun onPostExecute() {
        mPostTime = SystemClock.uptimeMillis()
        if (mWaitTimeoutMillis > 0) {
            mWatchHandler.sendEmptyMessageDelayed(MSG_CHECK_WAIT, mWaitTimeoutMillis)
        }
    }

    /**
     * 开始执行
     */
    fun onExecuteStart() {
        mStartTime = SystemClock.uptimeMillis()
        AkLog.t(TAG).v("$mName, wait ${mStartTime - mPostTime}")
        mWatchHandler.removeCallbacksAndMessages(null)
        if (mExecuteTimeoutMillis > 0) {
            mWatchHandler.sendEmptyMessageDelayed(MSG_CHECK_WORK, mExecuteTimeoutMillis)
        }
    }

    /**
     * 执行结束
     */
    fun onExecuteEnd() {
        val endTime = SystemClock.uptimeMillis()
        AkLog.t(TAG).v("$mName, execute cost ${endTime - mStartTime}")
        mWatchHandler.removeCallbacksAndMessages(null)
    }
}