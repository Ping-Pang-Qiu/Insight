package com.ak.framework.widget.topbar

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import com.ak.framework.R
import com.ak.framework.util.ext.setOnDebounceClickListener

class AkTopBar : AkTopBarFrameLayout {

    val tvTitle: TextView
    val ivLeftBackArrow: ImageView
    val ivRightMenu: ImageView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.AkTopBar, defStyleAttr, 0)
        val titleText = array.getString(R.styleable.AkTopBar_ak_topbar_title_text)
        val leftBackEnable =
            array.getBoolean(R.styleable.AkTopBar_ak_topbar_left_back_enable, false)
        val rightMenuEnable =
            array.getBoolean(R.styleable.AkTopBar_ak_topbar_right_menu_enable, false)
        array.recycle()

        // 填充布局
        LayoutInflater.from(context).inflate(R.layout.ak_topbar_layout, this, true)
        tvTitle = findViewById(R.id.tv_title)
        ivLeftBackArrow = findViewById(R.id.iv_back_arrow)
        ivRightMenu = findViewById(R.id.iv_menu)
        ivLeftBackArrow.setOnDebounceClickListener(OnClickListener {
            val context = getContext()
            if (context is Activity) {
                context.onBackPressed()
            }
        })

        if (titleText != null && titleText.isNotEmpty()) {
            tvTitle.text = titleText
        }

        ivLeftBackArrow.visibility = if (leftBackEnable) View.VISIBLE else View.GONE
        ivRightMenu.visibility = if (rightMenuEnable) View.VISIBLE else View.GONE
    }

    fun setTitle(text: String) {
        tvTitle.text = text
    }

    fun setTitle(@StringRes id: Int) {
        tvTitle.text = context.getString(id)
    }
}