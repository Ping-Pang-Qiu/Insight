package com.ak.insight

import com.ak.framework.developer.AkDeveloper
import com.ak.framework.app.AkApplication
import com.ak.framework.util.AkLog

class MainApplication : AkApplication() {

    override fun onCreate() {
        super.onCreate()
        AkLog.config(true, AkLog.VERBOSE)
        AkDeveloper.setEnable(false)
    }
}