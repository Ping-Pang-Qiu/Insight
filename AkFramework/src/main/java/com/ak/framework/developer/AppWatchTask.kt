package com.ak.framework.developer

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import com.ak.framework.app.AkAppState
import com.ak.framework.app.AkApplication
import com.ak.framework.util.AkLog
import com.ak.framework.task.AkHandler
import java.io.File

/**
 * 监测：sp、fd、thread
 */
internal class AppWatchTask : AkAppState.AkAppStateCallback {
    private companion object {
        private const val TAG = "AppWatchTask"
        private const val SP_FILE_MAX_SIZE = 50 * 1024 // 单个sp文件大小限制，单位：字节
        private const val SP_FILE_TOTAL_MAX_SIZE = 300 * 1024 // sp全部文件大小限制，单位：字节
        private const val FD_MAX_COUNT = 200 // 文件描述符最大限制
        private const val THREAD_MAX_COUNT = 200 // 线程数最大限制

        private const val MSG_WATCH_SHARED_PREFERENCE = 0x1 // sp文件
        private const val MSG_WATCH_FILE_DESCRIPTOR = 0x2 // fd文件描述符
        private const val MSG_WATCH_THREAD = 0x3 // fd文件描述符

        // 监测频率：时间间隔，单位：ms
        private const val INTERVAL_WATCH_FILE_DESCRIPTOR = 15 * 1000L //文件描述符扫描间隔
        private const val INTERVAL_WATCH_THREAD = 15 * 1000L //线程扫描间隔

        private val mWatchHandlerThread: HandlerThread = HandlerThread(TAG)
        private val mWatchLooper: Looper

        init {
            mWatchHandlerThread.start()
            mWatchLooper = mWatchHandlerThread.looper
        }
    }

    private val mWatchHandler: Handler
    private var mEnable: Boolean = false
    private var mStarted: Boolean = false

    private var mMaxFdCount = 0
    private var mMaxThreadCount = 0

    init {
        mWatchHandler = AkHandler("$TAG",
            mWatchLooper, Handler.Callback { msg ->
                if (msg.what == MSG_WATCH_SHARED_PREFERENCE) {
                    checkSP()
                } else if (msg.what == MSG_WATCH_FILE_DESCRIPTOR) {
                    checkFD()
                    sendEmptyMsgDelayed(
                        MSG_WATCH_FILE_DESCRIPTOR,
                        INTERVAL_WATCH_FILE_DESCRIPTOR
                    )
                } else if (msg.what == MSG_WATCH_THREAD) {
                    checkThread()
                    sendEmptyMsgDelayed(
                        MSG_WATCH_THREAD,
                        INTERVAL_WATCH_THREAD
                    )
                }
                true
            })

        AkAppState.registerAppStateCallback(this)
    }

    override fun onAppForeground() {
        if (mEnable) {
            start()
        }
    }

    override fun onAppBackground() {
        if (mEnable) {
            stop()
        }
    }

    fun setEnable(enable: Boolean) {
        mEnable = enable
        if (enable) {
            start()
        } else {
            stop()
        }
    }

    private fun start() {
        if (mStarted || !AkAppState.isAppForeground()) {
            return
        }
        AkLog.t(TAG).v("start")
        mStarted = true
        val delay = 2000L // 延迟启动，避免抢占资源
        mWatchHandler.removeCallbacksAndMessages(null)
        sendEmptyMsgDelayed(MSG_WATCH_SHARED_PREFERENCE, delay)
        sendEmptyMsgDelayed(MSG_WATCH_FILE_DESCRIPTOR, delay)
        sendEmptyMsgDelayed(MSG_WATCH_THREAD, delay)
    }

    private fun stop() {
        AkLog.t(TAG).v("stop")
        mStarted = false
        mWatchHandler.removeCallbacksAndMessages(null)
    }

    private fun checkSP() {
        val dataDir = AkApplication.appContext.filesDir.parent
        val spDir = File(dataDir, "shared_prefs")
        val files = spDir.listFiles()
        var totalSize = 0L
        for (sp in files) {
            val size = sp.length()
            if (size > SP_FILE_MAX_SIZE) {
                AkLog.t(TAG).e("sp is too big, name=${sp.name}, size=${size}")
            }
            totalSize += sp.length()
        }

        if (totalSize > SP_FILE_TOTAL_MAX_SIZE) {
            AkLog.t(TAG).e("sp is too big, total size=${totalSize}")
        }
    }

    private fun checkFD() {
        val path = String.format("/proc/%d/fd", Process.myPid())
        val file = File(path)
        if (!file.exists() || !file.canRead()) {
            return
        }

        val list = file.listFiles()
        if (list.size < FD_MAX_COUNT) {
            mMaxFdCount = Math.max(mMaxFdCount, list.size)
            AkLog.t(TAG).v("fd count=${list.size}, max=${mMaxFdCount}")
        } else {
            AkLog.t(TAG).e("fd count is too large, count=${list.size}, limit=$FD_MAX_COUNT")
        }
    }

    private fun checkThread() {
        var totalCount = getThreadCount()
        val jvmCount = Thread.getAllStackTraces().size

        if (totalCount < THREAD_MAX_COUNT) {
            mMaxThreadCount = Math.max(mMaxThreadCount, totalCount)
            AkLog.t(TAG).v("thread count=$totalCount, max=${mMaxThreadCount}")
        } else {
            AkLog.t(TAG).e("thread count is too large, total=$totalCount, jvm=$jvmCount, limit=$THREAD_MAX_COUNT")
        }
    }

    private fun sendEmptyMsgDelayed(what: Int, delay: Long) {
        if (mStarted) {
            mWatchHandler.sendEmptyMessageDelayed(what, delay)
        }
    }

    private fun getThreadCount(): Int {
        val path = String.format("/proc/%d/task", Process.myPid())
        val file = File(path)
        return if (!file.exists() || !file.canRead()) {
            0
        } else {
            file.list().size
        }
    }
}