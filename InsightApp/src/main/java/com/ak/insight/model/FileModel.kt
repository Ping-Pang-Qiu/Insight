package com.ak.insight.model

import java.io.File


object FileModel {
    data class FileSizeData(val file: File, var count: Int, var totalLength: Long)

    data class DirSizeData(val dirData: FileSizeData, val list: List<FileSizeData>)

    /**
     * 耗时操作
     */
    fun getFileSizeData(dir: File): FileSizeData {
        val data = FileSizeData(dir, 0, 0)
        recurFileInternal(dir, data)
        return data
    }

    /**
     * 耗时操作
     */
    fun getDirSizeData(dir: File): DirSizeData {
        val dirData = FileSizeData(dir, 0, 0)
        val list = mutableListOf<FileSizeData>()
        if (dir.isFile) {
            list.add(FileSizeData(dir, 1, dir.length()))
        } else {
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (f in files) {
                    list.add(getFileSizeData(f))
                }
            }
        }

        for (data in list) {
            dirData.count += data.count
            dirData.totalLength += data.totalLength
        }

        list.sortWith(Comparator { o1, o2 -> o1.file.name.compareTo(o2.file.name) })
        return DirSizeData(dirData, list)
    }

    private fun recurFileInternal(dir: File, data: FileSizeData) {
        if (dir.isFile) {
            data.count++
            data.totalLength += dir.length()
        } else {
            val files = dir.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (f in files) {
                    recurFileInternal(f, data)
                }
            }
        }
    }
}