package com.ak.framework.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ak.framework.app.AkActivity
import com.ak.framework.app.AkApplication
import com.ak.framework.app.AkFragment

object AkRouter {
    fun startActivitySafely(context: Context, intent: Intent) {
        try {
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            AkLog.e(e)
        }
    }

    /**
     * 跳转桌面app
     */
    fun startHomeApp(context: Context) {
        try {
            val context = AkApplication.appContext
            var intent = Intent(Intent.ACTION_MAIN)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(intent)
        } catch (e: Exception) {
            AkLog.e(e)
        }
    }

    /**
     * 跳转应用市场打分
     */
    fun gotoScore(context: Context) {
        val uri: Uri = Uri.parse("market://details?id=" + context.packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        AkRouter.startActivitySafely(context, intent)
    }
}