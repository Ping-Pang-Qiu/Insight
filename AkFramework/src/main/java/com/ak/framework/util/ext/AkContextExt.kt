package com.ak.framework.util.ext

import android.content.Context
import android.content.pm.PackageInfo
import com.ak.framework.util.AkLog
import com.ak.framework.util.os.AkBuildUtils
import java.io.File
import java.lang.Exception

fun Context.getAppDataDir(): File {
    return if (AkBuildUtils.AT_LEAST_24) {
        dataDir
    } else {
        filesDir.parentFile
    }
}

fun Context.getAppExternalDir(): File {
    return getExternalFilesDir(null)!!.parentFile
}

fun Context.isAppInstalled(pkgName: String?): Boolean {
    if (pkgName.isNullOrEmpty()) {
        return false
    }

    try {
        return packageManager.getPackageInfo(pkgName, 0) != null
    } catch (e: Exception) {
        AkLog.e(e)
    }

    return false
}
