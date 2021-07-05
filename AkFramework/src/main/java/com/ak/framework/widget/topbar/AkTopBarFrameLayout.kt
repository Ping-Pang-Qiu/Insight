package com.ak.framework.widget.topbar

import android.content.Context
import android.util.AttributeSet
import com.ak.framework.R
import com.ak.framework.widget.fitwindow.IFitWindowLayout
import com.ak.framework.widget.layout.AkFrameLayout

open class AkTopBarFrameLayout : AkFrameLayout {
    private var mContentHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContentHeight =
            context.resources.getDimensionPixelSize(R.dimen.ak_top_bar_content_height)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val insetTop = getInsetTop()
        setPadding(paddingLeft, insetTop, paddingRight, paddingBottom)
        val h = mContentHeight + insetTop
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY))
    }

    private fun getInsetTop(): Int {
        var parent = parent
        while (parent != null) {
            if (parent is IFitWindowLayout) {
                return parent.getInsetRect().top
            }

            parent = parent.parent
        }
        return 0
    }
}