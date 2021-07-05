package com.ak.framework.widget.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

open class AkFrameLayout : FrameLayout {
    private val mLayoutHelper: LayoutHelper

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mLayoutHelper = LayoutHelper(this, context, attrs, defStyleAttr)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val overrideHeight = mLayoutHelper.getMeasureHeight()
        if (overrideHeight > 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(overrideHeight, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}