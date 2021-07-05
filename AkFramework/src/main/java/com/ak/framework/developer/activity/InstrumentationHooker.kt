package com.ak.framework.developer.activity

import android.app.Instrumentation
import com.ak.framework.util.AkLog
import java.lang.Exception
import java.lang.reflect.Field

object InstrumentationHooker {
    private const val TAG = "InstrumentationHooker"
    private var mHookSucceed: Boolean = false

    fun hook() {
        if (mHookSucceed) {
            return
        }

        try {
            hookInstrumentation()
            mHookSucceed = true
        } catch (e: Exception) {
            AkLog.t(TAG).e(e, "hook error")
        }
    }

    fun isHookSucceed(): Boolean {
        return mHookSucceed
    }

    private fun hookInstrumentation() {
        val activityThreadCls = Class.forName("android.app.ActivityThread")
        val currentActivityThread = activityThreadCls.getDeclaredMethod("currentActivityThread")

        var acc = currentActivityThread.isAccessible
        if (!acc) {
            currentActivityThread.isAccessible = true
        }
        // 获取主线程对象
        val activityThreadObject = currentActivityThread.invoke(null)
        if (!acc) {
            currentActivityThread.isAccessible = acc
        }

        //获取Instrumentation字段
        val instrumentationField: Field = activityThreadCls.getDeclaredField("mInstrumentation")
        acc = instrumentationField.isAccessible
        if (!acc) {
            instrumentationField.isAccessible = true
        }

        // 把系统的instrumentation替换为自己的Instrumentation对象
        val curInstrumentation = instrumentationField.get(activityThreadObject) as Instrumentation
        val newInstrumentation = ApmInstrumentation(curInstrumentation)
        instrumentationField.set(activityThreadObject, newInstrumentation)
        if (!acc) {
            instrumentationField.isAccessible = acc
        }
    }
}