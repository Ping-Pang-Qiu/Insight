package com.ak.insight.fragment

import android.content.Context
import android.view.View
import android.widget.TextView
import com.ak.framework.app.AkFragment
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.TargetApp
import com.ak.insight.R
import java.lang.StringBuilder


class SpEditFragment : AkFragment() {
    companion object {
        private const val TAG = "SpEditFragment"

        fun createFragment(name: String, key: String): AkFragment {
            val fragment = SpEditFragment()
            fragment.mSpName = name
            fragment.mKey = key
            return fragment
        }
    }

    private enum class ValueType(val typeName: String) {
        STRING("String"),
        INT("Int"),
        Float("Float"),
        Boolean("Boolean"),
        Long("Long");

        companion object {
            fun getType(obj: Any): ValueType {
                return when (obj) {
                    is Int -> INT
                    is kotlin.Long -> Long
                    is kotlin.Boolean -> Boolean
                    is kotlin.Float -> Float
                    else -> STRING
                }
            }
        }

    }

    private lateinit var mSpName: String
    private lateinit var mKey: String
    private lateinit var mType: ValueType

    override fun getLayoutResId(): Int {
        return R.layout.fragment_sp_edit
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle(mKey)


        val sp = TargetApp.targetContext.getSharedPreferences(mSpName, Context.MODE_PRIVATE)
        val valueObj = sp.all[mKey]
        mType = ValueType.getType(valueObj!!)


        val tvInfo: TextView = root.findViewById(R.id.tv_info)
        val infoBuilder = StringBuilder()
        infoBuilder.append("类型：${mType.typeName}")
        if (mType != ValueType.Boolean) {
            infoBuilder.append("\n长度：${valueObj.toString().length}")
        }
        tvInfo.text = infoBuilder.toString()

        val tvContent: TextView = root.findViewById(R.id.tv_content)
        tvContent.text = valueObj.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}