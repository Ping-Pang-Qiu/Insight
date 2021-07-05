package com.ak.framework.developer

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import com.ak.framework.app.AkAppState
import com.ak.framework.util.AkLog
import com.ak.framework.util.AkToast
import com.ak.framework.task.AkHandler
import java.util.concurrent.atomic.AtomicInteger

internal class MainThreadWatchTask : AkAppState.AkAppStateCallback {
    private companion object {
        private const val DEBUG = false
        private const val TAG = "MainThreadWatchTask"

        private const val DEFAULT_WATCH_TIMEOUT = 100L

        private const val MSG_WATCH_COUNT = 0x1
        private const val MSG_INCREASE_COUNT = 0x2

        private val mWatchHandlerThread: HandlerThread = HandlerThread(TAG)
        private val mWatchLooper: Looper

        init {
            mWatchHandlerThread.start()
            mWatchLooper = mWatchHandlerThread.looper
        }
    }


    private val mMainHandler: Handler
    private val mWatchHandler: Handler

    private var mEnable: Boolean = false
    private var mStarted: Boolean = false


    private val mTimeoutMillis: Long =
        DEFAULT_WATCH_TIMEOUT

    @Volatile
    private var mWatchCounter = AtomicInteger()


    init {
        mWatchHandler = AkHandler("$TAG",
            mWatchLooper, Handler.Callback { msg ->
                if (msg.what == MSG_WATCH_COUNT) {
                    val target = msg.arg1
                    val retry = msg.arg2
                    val sendTime: Long = msg.obj as Long
                    if (DEBUG) {
                        AkLog.t(TAG).v("target = $target, retry = $retry")
                    }

                    if (target > mWatchCounter.get()) {
                        sendWatchMessage(mWatchCounter.get() + 1, retry + 1)
                        val delay = SystemClock.uptimeMillis() - sendTime - mTimeoutMillis
                        val dur = (retry + 1) * mTimeoutMillis
                        triggerANR(dur, delay)
                    } else {
                        if (DEBUG) {
                            AkLog.t(TAG).v("NORMAL, watch millis $mTimeoutMillis")
                        }
                        sendWatchMessage(mWatchCounter.get() + 1, 0)
                        sendIncreaseMessage()
                    }
                }

                true
            })
        mMainHandler = AkHandler("$TAG", Looper.getMainLooper(), Handler.Callback { msg ->
            if (msg.what == MSG_INCREASE_COUNT) {
                mWatchCounter.getAndIncrement()
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

    /**
     * 开始监测
     */
    private fun start() {
        if (mStarted || !AkAppState.isAppForeground()) {
            return
        }

        AkLog.t(TAG).v("start")
        mStarted = true
        mWatchHandler.removeCallbacksAndMessages(null)
        mWatchCounter.set(0)
        sendWatchMessage(0, 0)
    }

    /**
     * 停止监测
     */
    private fun stop() {
        if (!mStarted) {
            return
        }

        AkLog.t(TAG).v("stop")
        mStarted = false
    }

    private fun sendWatchMessage(target: Int, retry: Int) {
        if (!mStarted) {
            return
        }

        mWatchHandler.removeMessages(MSG_WATCH_COUNT)
        mWatchHandler.sendMessageDelayed(
            mWatchHandler.obtainMessage(MSG_WATCH_COUNT, target, retry, SystemClock.uptimeMillis()),
            mTimeoutMillis
        )
    }

    private fun sendIncreaseMessage() {
        mMainHandler.removeMessages(MSG_INCREASE_COUNT)
        mMainHandler.obtainMessage(MSG_INCREASE_COUNT).sendToTarget()
    }

    private fun triggerANR(duration: Long, watchDelay: Long) {
        val msg = "main thread timeout $duration, delay $watchDelay"
        AkLog.t(TAG).w(msg)
        AkToast.show(msg)
    }
}