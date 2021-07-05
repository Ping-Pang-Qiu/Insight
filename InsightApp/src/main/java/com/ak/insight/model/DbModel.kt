package com.ak.insight.model

import com.ak.insight.TargetApp
import java.io.File

object DbModel {
    data class DbListData(val dir: File, val files: List<File>, val totalLength: Long)

    fun loadDbListData(): DbListData {
        val dataDir = TargetApp.targetContext.filesDir.parent
        val dbDir = File(dataDir, "databases")
        val files = dbDir.listFiles()
        if (files == null || files.isEmpty()) {
            return DbListData(
                dbDir,
                mutableListOf(),
                0
            )
        }

        val list = mutableListOf<File>()
        var totalSize = 0L
        for (f in files) {
            if (f.name.endsWith(".db")) {
                totalSize += f.length()
                list.add(f)
            }
        }

        list.sort()
        return DbListData(
            dbDir,
            list,
            totalSize
        )
    }
}