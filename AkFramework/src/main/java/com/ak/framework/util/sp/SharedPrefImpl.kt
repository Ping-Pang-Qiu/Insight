package com.ak.framework.util.sp

import android.content.Context
import android.content.SharedPreferences
import com.ak.framework.app.AkApplication

internal class SharedPrefImpl(name: String) : ISharedPreference {
    private val mSp: SharedPreferences by lazy {
        AkApplication.appContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
    private val mEditor: SharedPreferences.Editor by lazy { mSp.edit() }

    override fun putString(key: String, value: String): SharedPrefImpl {
        mEditor.putString(key, value)
        return this
    }

    override fun getString(key: String, defValue: String): String {
        return mSp.getString(key, defValue)!!
    }

    override fun putBoolean(key: String, value: Boolean): SharedPrefImpl {
        mEditor.putBoolean(key, value)
        return this
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSp.getBoolean(key, defValue)
    }

    override fun putInt(key: String, value: Int): ISharedPreference {
        mEditor.putInt(key, value)
        return this
    }

    override fun getInt(key: String, defValue: Int): Int {
        return mSp.getInt(key, defValue)
    }

    override fun putFloat(key: String, value: Float): ISharedPreference {
        mEditor.putFloat(key, value)
        return this
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return mSp.getFloat(key, defValue)
    }

    override fun putLong(key: String, value: Long): ISharedPreference {
        mEditor.putLong(key, value)
        return this
    }

    override fun getLong(key: String, defValue: Long): Long {
        return mSp.getLong(key, defValue)
    }

    override fun apply() {
        mEditor.apply()
    }

    override fun commit() {
        mEditor.commit()
    }
}