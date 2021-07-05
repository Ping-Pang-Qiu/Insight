package com.ak.insight.model

import com.ak.insight.TargetApp
import java.io.File

object SpModel {
    data class SpListData(val dir: File, val files: List<File>, val totalLength: Long)

    fun loadSpListData(): SpListData {
        val dataDir = TargetApp.targetContext.filesDir.parent
        val spDir = File(dataDir, "shared_prefs")
        val files = spDir.listFiles()
        if (files == null || files.isEmpty()) {
            return SpListData(
                spDir,
                mutableListOf(),
                0
            )
        }

        val list = files.toMutableList()
        var totalSize = 0L
        for (sp in list) {
            totalSize += sp.length()
        }

        list.sort()
        return SpListData(
            spDir,
            list,
            totalSize
        )
    }

}