package com.ak.framework.widget.fitwindow

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

class AkFitWindowFrameLayout : FrameLayout,
    IFitWindowLayout {
    private var mInsets: Rect = Rect()
    private var mOnWindowFitListener: IFitWindowLayout.OnWindowFitListener? = null

    companion object {
        fun wrap(context: Context, @LayoutRes layoutResID: Int): AkFitWindowFrameLayout {
            val layout = AkFitWindowFrameLayout(context)
            val child = LayoutInflater.from(context).inflate(layoutResID, layout, false)
            layout.addView(
                child,
                LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            )
            return layout
        }

        fun wrap(context: Context, child: View): AkFitWindowFrameLayout {
            val layout = AkFitWindowFrameLayout(context)
            layout.addView(
                child,
                LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
            )
            return layout
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun fitSystemWindows(insets: Rect?): Boolean {
        if (insets == null) {
            return false
        }

        mOnWindowFitListener?.onFitSystemWindows(insets)
        mInsets = insets
        return true
    }

    override fun setOnWindowFitListener(listener: IFitWindowLayout.OnWindowFitListener) {
        mOnWindowFitListener = listener
    }

    override fun getInsetRect(): Rect {
        return mInsets
    }
}