package com.ak.framework.util.os

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ak.framework.app.AkApplication

object AkScreen {
    val screenWidthDp: Int
    val screenWidthPx: Int
    val screenHeightDp: Int
    val screenHeightPx: Int

    init {
        val display = DisplayMetrics();
        (AkApplication.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getRealMetrics(display)
        screenWidthPx = display.widthPixels
        screenHeightPx = display.heightPixels
        screenWidthDp = (screenWidthPx / display.density).toInt()
        screenHeightDp = (screenHeightPx / display.density).toInt()
    }
}