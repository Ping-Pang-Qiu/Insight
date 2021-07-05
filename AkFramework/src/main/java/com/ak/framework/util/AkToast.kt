package com.ak.framework.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.ak.framework.R
import com.ak.framework.app.AkApplication
import com.ak.framework.widget.AkTextView

object AkToast {
    private var mToast: Toast? = null

    fun show(@StringRes res: Int) {
        show(AkApplication.appContext.getString(res))
    }

    fun show(text: String) {
        var context = AkApplication.appContext
        val toast = Toast(context)
        toast.view = createContentView(context, text)
        toast.setGravity(Gravity.CENTER, 0, 0)
        show(toast)
    }

    fun show(view: View) {
        var context = AkApplication.appContext
        val toast = Toast(context)
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        show(toast)
    }

    fun show(toast: Toast) {
        cancel()
        mToast = toast
        toast.show()
    }

    private fun cancel() {
        mToast?.cancel()
        mToast = null
    }

    private fun createContentView(context: Context, text: String): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.ak_toast_layout_dark, null)
        contentView.findViewById<AkTextView>(R.id.tv_title).text = text
        return contentView
    }
}