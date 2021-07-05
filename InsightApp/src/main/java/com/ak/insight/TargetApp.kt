package com.ak.insight

import android.app.ActivityManager
import android.content.Context
import com.ak.framework.app.AkApplication

object TargetApp {
    const val TARGET_PACKAGE_NAME = "com.ak.demo"

    val targetContext: Context by lazy {
        AkApplication.appContext.createPackageContext(
            TARGET_PACKAGE_NAME,
            Context.CONTEXT_IGNORE_SECURITY
        )
    }

    fun getTargetPid(): Int {
        val am = targetContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.runningAppProcesses
        for (info in list) {
            if (info.processName == TARGET_PACKAGE_NAME) {
                return info.pid
            }
        }
        return -1
    }
}