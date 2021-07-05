package com.ak.framework.developer.activity

import android.app.Activity
import android.app.Instrumentation
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import com.ak.framework.util.AkLog

class ApmInstrumentation(private val mDelegate: Instrumentation) : Instrumentation() {
    private companion object {
        private const val TAG = "ApmInstrumentation"
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle?) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnCreate(activity, icicle)
        val cost = SystemClock.uptimeMillis() - start
        if (cost > 100) {
            AkLog.t(TAG).w("${activity.componentName.className}, onCreate, cost $cost")
        }
    }

    override fun callActivityOnCreate(
        activity: Activity, icicle: Bundle?, persistentState: PersistableBundle?
    ) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnCreate(activity, icicle, persistentState)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onCreate", cost)
    }

    override fun callActivityOnStart(activity: Activity) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnStart(activity)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onStart", cost)
    }

    override fun callActivityOnResume(activity: Activity) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnResume(activity)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onResume", cost)
    }

    override fun callActivityOnPause(activity: Activity) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnResume(activity)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onPause", cost)
    }


    override fun callActivityOnStop(activity: Activity) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnResume(activity)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onStop", cost)
    }

    override fun callActivityOnDestroy(activity: Activity) {
        val start = SystemClock.uptimeMillis()
        mDelegate.callActivityOnResume(activity)
        val cost = SystemClock.uptimeMillis() - start
        checkPerformance(activity, "onDestroy", cost)
    }

    private fun checkPerformance(activity: Activity, method: String, cost: Long) {
        if (cost > 30) {
            AkLog.t(TAG).w("${activity.componentName.className}, $method, cost $cost")
        }
    }
}