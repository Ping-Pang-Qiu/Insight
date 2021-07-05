package com.ak.framework.util.io

import java.io.File


object AkFileUtils {

    fun isVideoFile(file: File): Boolean {
        val reg = Regex("(?i).+?\\.(mp4|3gp|flv|avi)")
        return file.name.matches(reg)
    }

    fun isImageFile(file: File): Boolean {
        val reg = Regex("(?i).+?\\.(jpg|png|bmp|gif)")
        return file.name.matches(reg)
    }

}