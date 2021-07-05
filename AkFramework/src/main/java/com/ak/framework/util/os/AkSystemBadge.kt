package com.ak.framework.util.os

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.ak.framework.app.AkApplication
import com.ak.framework.util.AkApkInfo
import com.ak.framework.util.AkLog


object AkSystemBadge {
    fun setNumber(num: Int) {
        val component = AkApkInfo.launcherComponent ?: return
        val context = AkApplication.appContext
        if (AkDevice.brand == AkDevice.BRAND_HUAWEI) {
            setNumHuawei(context, num, component)
        }
    }

    private fun setNumHuawei(context: Context, num: Int, component: ComponentName) {
        try {

            val bundle = Bundle()
            bundle.putString("package", context.packageName)
            bundle.putString("class", component.className)
            bundle.putInt("badgenumber", num)
            context.contentResolver
                .call(
                    Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                    "change_badge", null, bundle
                )
        } catch (e: Exception) {
            AkLog.e(e)
        }
    }
}