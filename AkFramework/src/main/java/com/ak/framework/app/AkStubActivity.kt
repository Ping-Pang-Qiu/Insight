package com.ak.framework.app

import android.content.Context
import android.content.Intent
import com.ak.framework.util.AkRouter


open class AkStubActivity : AkActivity() {

    companion object {
        fun startWithFragment(context: Context, cls: Class<out AkFragment>) {
            val intent = Intent(context, AkStubActivity::class.java)
            intent.putExtra(KEY_FIRST_FRAGMENT_CLASS_NAME, cls.name)
            AkRouter.startActivitySafely(context, intent)
        }
    }
}