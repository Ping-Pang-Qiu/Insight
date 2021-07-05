package com.ak.framework.widget.topbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ak.framework.widget.fitwindow.IFitWindowLayout

class AkStatusBarOccupyView : View {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, getInsetTop())
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