package com.ak.framework.util.os

import android.os.Build

object AkBuildUtils {
    inline val AT_LEAST_19: Boolean
        get() = Build.VERSION.SDK_INT >= 19

    inline val AT_LEAST_21: Boolean
        get() = Build.VERSION.SDK_INT >= 21

    inline val AT_LEAST_23: Boolean
        get() = Build.VERSION.SDK_INT >= 23

    inline val AT_LEAST_24: Boolean
        get() = Build.VERSION.SDK_INT >= 24

    inline val AT_LEAST_26: Boolean
        get() = Build.VERSION.SDK_INT >= 26

    inline val AT_LEAST_27: Boolean
        get() = Build.VERSION.SDK_INT >= 27

    inline val AT_LEAST_28: Boolean
        get() = Build.VERSION.SDK_INT >= 28
}