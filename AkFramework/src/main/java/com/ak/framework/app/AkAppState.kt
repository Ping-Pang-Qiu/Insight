package com.ak.framework.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.ak.framework.developer.AkDeveloper
import com.ak.framework.developer.activity.ActivityMonitor
import com.ak.framework.util.AkLog
import com.ak.framework.util.sp.AkSharedPreference

/**
 * 应用状态： 前后台
 */
object AkAppState {

    interface AkAppStateCallback {
        fun onAppForeground()

        fun onAppBackground()
    }

    private const val TAG = "AkAppState"
    private const val SP_FILE_NAME = "ak_app_state"
    private const val KEY_APP_FOREGROUND_STATE = "app_foreground_state"

    private val mAppLifecycleCallback = AppLifecycleCallback()
    private val mCallbackSet = mutableSetOf<AkAppStateCallback>()

    private var mIsForeground: Boolean = false
    private var mResumedActivity: Activity? = null

    fun init(application: AkApplication) {
        application.registerActivityLifecycleCallbacks(mAppLifecycleCallback)
    }

    fun isAppForeground(): Boolean {
        return mIsForeground
    }

    fun registerAppStateCallback(callback: AkAppStateCallback) {
        mCallbackSet.add(callback)
    }

    fun unregisterAppStateCallback(callback: AkAppStateCallback) {
        mCallbackSet.remove(callback)
    }

    private fun onAppForeground() {
        AkLog.t(TAG).i("onAppForeground")
        mIsForeground = true
        saveAppForegroundState(true)
        mCallbackSet.forEach {
            it.onAppForeground()
        }
    }

    private fun onAppBackground() {
        AkLog.t(TAG).i("onAppBackground")
        mIsForeground = false
        saveAppForegroundState(false)
        mCallbackSet.forEach {
            it.onAppBackground()
        }
    }


    private fun saveAppForegroundState(foreground: Boolean) {
        AkSharedPreference.get(SP_FILE_NAME).putBoolean(KEY_APP_FOREGROUND_STATE, foreground).apply()
    }

    fun getLastAppForegroundState(): Boolean {
        return AkSharedPreference.get(SP_FILE_NAME).getBoolean(KEY_APP_FOREGROUND_STATE, false)
    }

    private class AppLifecycleCallback : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (AkDeveloper.isEnable()) {
                ActivityMonitor.onActivityCreated(activity)
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            if (mResumedActivity == null) {
                onAppForeground();
            }
            mResumedActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
            if (mResumedActivity == activity) {
                mResumedActivity = null;
                onAppBackground()
            }
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }
}