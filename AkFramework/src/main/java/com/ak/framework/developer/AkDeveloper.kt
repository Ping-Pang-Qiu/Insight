package com.ak.framework.developer

import com.ak.framework.developer.activity.InstrumentationHooker


object AkDeveloper {
    private var mEnable = false

    fun setEnable(enable: Boolean) {
        if (mEnable == enable) {
            return
        }

        mEnable = enable
        if (enable) {
            InstrumentationHooker.hook()
        }
    }

    /**
     * 开发者模式： 强制代码规范、性能问题提示
     */
    fun isEnable(): Boolean {
        return mEnable
    }
}