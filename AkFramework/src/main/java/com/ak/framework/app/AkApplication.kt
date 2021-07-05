package com.ak.framework.app

import android.app.Application
import android.content.Context

open class AkApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        AkAppState.init(this)
    }
}