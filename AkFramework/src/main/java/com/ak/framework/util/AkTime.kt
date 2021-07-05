package com.ak.framework.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * y:年、M:月、d:日、H:24进制小时、h:12进制小时、m:分、s:秒、S:毫秒
 */
object AkTime {

    private val dayFormat: SimpleDateFormat = SimpleDateFormat("yyyyMMdd")

    fun getTimeInDay(): Int {
        return dayFormat.format(Date()).toInt()
    }
}



