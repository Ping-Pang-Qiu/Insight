package com.ak.framework.widget.fitwindow

import android.graphics.Rect

interface IFitWindowLayout {
    fun getInsetRect(): Rect

    fun setOnWindowFitListener(listener: OnWindowFitListener)

    interface OnWindowFitListener {
        fun onFitSystemWindows(insets: Rect)
    }
}