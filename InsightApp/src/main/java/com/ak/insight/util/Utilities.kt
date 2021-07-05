package com.ak.insight.util

object Utilities {

    fun formatFileLength(length: Long): String {
        val numK: Float = 1.0f * length / 1024
        return if (numK > 1024) {
            "%.3f".format(numK / 1024) + "m"
        } else {
            "%.3f".format(numK) + "k"
        }
    }

    fun formatMemorySize(length: Int): String {
        return "%.3f".format(1.0f * length / 1024) + "m"
    }
}