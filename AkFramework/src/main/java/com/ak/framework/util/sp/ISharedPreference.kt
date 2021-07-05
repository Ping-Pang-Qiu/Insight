package com.ak.framework.util.sp

interface ISharedPreference {

    fun putString(key: String, value: String): ISharedPreference

    fun getString(key: String, defValue: String): String

    fun putBoolean(key: String, value: Boolean): ISharedPreference

    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun putInt(key: String, value: Int): ISharedPreference

    fun getInt(key: String, defValue: Int): Int

    fun putFloat(key: String, value: Float): ISharedPreference

    fun getFloat(key: String, defValue: Float): Float

    fun putLong(key: String, value: Long): ISharedPreference

    fun getLong(key: String, defValue: Long): Long

    fun apply()

    fun commit()
}