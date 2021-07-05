package com.ak.framework.util.sp

import java.lang.ref.WeakReference

object AkSharedPreference {
    private val mCacheMap: MutableMap<String, WeakReference<ISharedPreference>> = mutableMapOf()

    fun get(name: String): ISharedPreference {
        val weakRef = mCacheMap[name]
        if (weakRef?.get() != null) {
            return weakRef.get()!!
        }

        val pref: ISharedPreference = SharedPrefImpl(name)
        mCacheMap[name] = WeakReference(pref)
        return pref
    }
}