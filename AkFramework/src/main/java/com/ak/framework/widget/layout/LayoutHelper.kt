package com.ak.framework.widget.layout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.ak.framework.R

internal class LayoutHelper {
    private val mViewGroup: ViewGroup

    constructor(viewGroup: ViewGroup, context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mViewGroup = viewGroup
        val a = context.obtainStyledAttributes(attrs, R.styleable.AkLayout, defStyleAttr, 0)
        a.recycle()
    }

    fun getMeasureHeight(): Int {
        return 0
    }
}