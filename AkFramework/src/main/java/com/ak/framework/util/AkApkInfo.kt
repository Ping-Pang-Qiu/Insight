package com.ak.framework.util

import android.content.ComponentName
import android.content.Intent
import com.ak.framework.app.AkApplication

object AkApkInfo {
    val versionName: String by lazy {
        initVersionName()
    }

    val versionCode: Int by lazy {
        initVersionCode()
    }

    val launcherComponent: ComponentName? by lazy { initLauncherComponent() }

    private fun initVersionName(): String {
        try {
            val context = AkApplication.appContext
            val pm = context.packageManager
            val info = pm.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: Exception) {

        }
        return ""
    }

    private fun initVersionCode(): Int {
        try {
            val context = AkApplication.appContext
            val pm = context.packageManager
            val info = pm.getPackageInfo(context.packageName, 0)
            return info.versionCode
        } catch (e: Exception) {

        }
        return 0
    }

    private fun initLauncherComponent(): ComponentName? {
        try {
            val context = AkApplication.appContext
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(context.packageName)

            val list = pm.queryIntentActivities(intent, 0)
            for (info in list) {
                if (info?.activityInfo != null) {
                    return ComponentName(context.packageName, info.activityInfo.name)
                }
            }
        } catch (e: Exception) {

        }
        return null
    }
}