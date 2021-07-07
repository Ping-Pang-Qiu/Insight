package com.ak.insight.widget

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.ak.framework.util.ext.dp2Px
import com.ak.framework.util.os.AkScreen
import com.ak.framework.widget.AkTextView
import com.ak.framework.widget.layout.AkLinearLayout
import com.ak.insight.R
import com.ak.insight.model.DbSQLiteModel

class DbTableRowLayout : AkLinearLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        orientation = HORIZONTAL
    }

    fun fillColumnInfoAsTitle(columnInfoList: List<DbSQLiteModel.ColumnInfo>) {
        if (childCount > 0) {
            return
        }

        val colCount = columnInfoList.size
        val minWidth = (AkScreen.screenWidthPx / colCount).coerceAtLeast(80.dp2Px())

        val extraSpace = 15.dp2Px()
        val paddingLeft = 12.dp2Px()
        val textSize = 15.dp2Px().toFloat()
        var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.typeface = Typeface.DEFAULT_BOLD

        textPaint.textSize = textSize
        val textColor = context.resources.getColor(R.color.ak_color_txt_h1_black)

        (columnInfoList.indices).forEach {
            val info = columnInfoList[it]
            val textView = AkTextView(context)
            val widthPx =
                minWidth.coerceAtLeast((textPaint.measureText(info.name) + extraSpace).toInt())
            val layoutParams = LayoutParams(widthPx, LayoutParams.MATCH_PARENT)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setTextColor(textColor)
            textView.setPadding(paddingLeft, 0, 0, 0)
            textView.isSingleLine = true
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            addView(textView, layoutParams)
        }
    }

    fun setTextOfIndex(index: Int, text: String) {
        if (index < 0 || index >= childCount) {
            return
        }

        (getChildAt(index) as TextView).text = text
    }

    fun cloneChildrenWithWidth(layout: DbTableRowLayout) {
        if (childCount > 0) {
            return
        }

        val textSize = 14.dp2Px().toFloat()
        val paddingLeft = 10.dp2Px()
        val textColor = context.resources.getColor(R.color.ak_color_txt_h1_black)
        val count = layout.childCount
        (0 until count).forEach {
            val width = layout.getChildAt(it).layoutParams.width
            val layoutParams = LayoutParams(width, LayoutParams.MATCH_PARENT)
            val textView = AkTextView(context)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setTextColor(textColor)
            textView.isSingleLine = true
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.setPadding(paddingLeft, 0, 0, 0)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            addView(textView, layoutParams)
        }
    }
}