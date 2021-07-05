package com.ak.framework.util.io

import android.database.Cursor
import com.ak.framework.util.AkLog
import java.lang.Exception

object AkIOUtils {

    fun closeSilently(cursor: Cursor?) {
        try {
            cursor?.close()
        } catch (e: Exception) {
            AkLog.e(e)
        }
    }
}