package com.ak.insight.model


import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import com.ak.insight.TargetApp
import java.io.File

object ProcessModel {

    fun getThreadCount(pid: Int): Int {
        val path = String.format("/proc/%d/task", pid)
        val file = File(path)
        return if (!file.exists() || !file.canRead()) {
            0
        } else {
            file.list().size
        }
    }


    fun getFdCount(pid: Int): Int {
        val path = String.format("/proc/%d/fd", pid)
        val file = File(path)
        return if (!file.exists() || !file.canRead()) {
            0
        } else {
            file.list().size
        }
    }

    /**
     * VSS - Virtual Set Size 虚拟耗用内存（包含共享库占用的内存）
     * RSS - Resident Set Size 实际使用物理内存（包含共享库占用的内存）
     * PSS - Proportional Set Size 实际使用的物理内存（比例分配共享库占用的内存)
     * USS - Unique Set Size 进程独自占用的物理内存（不包含共享库占用的内存）
     * 一般来说内存占用大小有如下规律：VSS >= RSS >= PSS >= USS
     *
     * https://www.zhihu.com/question/67399411
     */
    fun getMemoryInfo(pid: Int): Debug.MemoryInfo {
        val am =
            TargetApp.targetContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo: Array<Debug.MemoryInfo> = am.getProcessMemoryInfo(intArrayOf(pid))
        return memoryInfo[0]
    }
}