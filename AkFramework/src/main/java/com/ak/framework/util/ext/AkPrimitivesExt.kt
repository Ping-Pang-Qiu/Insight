package com.ak.framework.util.ext

import com.ak.framework.app.AkApplication

private val density: Float by lazy {
    AkApplication.appContext.resources.displayMetrics.density
}

fun Int.dp2Px(): Int {
    return (density * this + 0.5f).toInt()
}

fun Int.px2Dp(): Int {
    return (this / density + 0.5f).toInt()
}

fun Float.dp2Px(): Float {
    return density * this + 0.5f
}

fun Float.px2Dp(): Float {
    return this / density + 0.5f
}
