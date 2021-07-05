package com.ak.framework.widget.layout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 优缺点:
 * 1. 可以用来减少布局层级
 * 2. 解决复杂界面布局功能强大
 * 3. 非复杂界面耗时比LinearLayout 和RelativeLayout多，因此简单布局应使用线性或相对布局
 * 4. RecyclerView子View尽量避免使用ConstraintLayout ，性能有问题
 */
open class AkConstraintLayout : ConstraintLayout {
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
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(overrideHeight, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}