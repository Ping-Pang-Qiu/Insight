package com.ak.framework.developer.activity

import android.app.Activity
import android.graphics.RectF
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.ak.framework.app.AkActivity
import com.ak.framework.util.AkLog
import com.ak.framework.widget.fitwindow.IFitWindowLayout
import java.lang.StringBuilder

/**
 *
 * 监测：触摸事件、帧率
 */
internal object ActivityMonitor : Choreographer.FrameCallback {
    private const val TAG = "ActivityMonitor"

    private var mStartDrawTime: Long = 0
    private var mTouchTraceEnable: Boolean = false

    fun onActivityCreated(activity: Activity) {
        if (activity !is AkActivity) {
            AkLog.t(TAG).e("${activity.componentName.className}, not use AkActivity")
        }
    }

    fun onDraw() {
        mStartDrawTime = System.nanoTime()
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        val millis: Long = (frameTimeNanos - mStartDrawTime) / 1000000
        if (millis > 100) {
            AkLog.t(TAG).e("draw timeout $millis")
        } else if (millis > 50) {
            AkLog.t(TAG).w("draw timeout $millis")
        } else if (millis > 30) {
            AkLog.t(TAG).d("draw cost $millis")
        } else if (millis > 16) {
            AkLog.t(TAG).v("draw cost $millis")
        }
    }

    fun onDispatchTouchEvent(activity: AkActivity, event: MotionEvent) {
        if (!mTouchTraceEnable) {
            return
        }

        if (event.action != MotionEvent.ACTION_DOWN) {
            return
        }

        val target =
            findTargetView(
                activity.getRootView(),
                event,
                0f,
                0f
            )
        if (target == null) {
            AkLog.t(TAG).e("can not find touch view")
        } else {
            AkLog.t(TAG).v(
                "has a touch event${parseViewPathToString(
                    target,
                    activity
                )}"
            )
        }
    }

    private fun parseViewPathToString(view: View, activity: AkActivity): String {
        val builder = StringBuilder()
        builder.append("\nActivity: ${activity.componentName.className}")
        var target = view;
        var index = 0
        while (true) {
            builder.append("\n${index++}: ${target.javaClass.name}")
            val parent = target.parent
            if (parent == null || parent !is View || parent is IFitWindowLayout) {
                break
            }

            target = parent
        }
        return builder.toString()
    }

    private fun findTargetView(root: View, event: MotionEvent, x: Float, y: Float): View? {
        if (root !is ViewGroup) {
            return root
        }
        val rect = RectF()
        val count = root.childCount
        var findView: View? = null
        for (index in 0 until count) {
            val child = root.getChildAt(index)
            if (child.visibility != View.VISIBLE) {
                continue
            }

            val width = child.width
            val height = child.height
            rect.top = 0f.coerceAtLeast(y + child.y)
            rect.left = 0f.coerceAtLeast(x + child.x)
            rect.right = rect.left + width
            rect.bottom = rect.top + height

            if (rect.contains(event.x, event.y)) {
                if (findView == null || findView.z < child.z) {
                    findView = child
                }
            }
        }

        return if (findView == null) {
            root
        } else {
            findTargetView(
                findView,
                event,
                x + findView.x,
                y + findView.y
            )
        }
    }
}