package com.ak.framework.util.ext

import android.os.SystemClock
import android.view.View

fun View.setOnDebounceClickListener(listener: View.OnClickListener) {
    this.setOnClickListener(OnDebounceClickListener(listener))
}

fun View.setOnMultiClickListener(listener: View.OnClickListener, count: Int) {
    this.setOnClickListener(OnMultiClickListener(listener, count))
}

class OnDebounceClickListener(private val listener: View.OnClickListener) :
    View.OnClickListener {

    private companion object {
        const val DEFAULT_DEBOUNCE_MILLIS = 300
    }

    private var time: Long = 0

    override fun onClick(v: View?) {
        val curTime = SystemClock.elapsedRealtime()
        if (curTime - time > DEFAULT_DEBOUNCE_MILLIS) {
            time = SystemClock.elapsedRealtime()
            listener.onClick(v)
        }
    }
}

class OnMultiClickListener(private val listener: View.OnClickListener, private val count: Int) :
    View.OnClickListener {

    private companion object {
        const val MILLIS_TIMEOUT = 300
    }

    private var time: Long = 0
    private var num: Int = 0

    override fun onClick(v: View?) {
        val curTime = SystemClock.elapsedRealtime()
        if (curTime - time > MILLIS_TIMEOUT) {
            num = 1
        } else {
            num++
        }

        time = curTime
        if (num == count) {
            listener.onClick(v)
        }
    }
}
