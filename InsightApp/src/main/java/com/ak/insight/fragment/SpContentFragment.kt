package com.ak.insight.fragment

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.TargetApp
import com.ak.insight.R
import com.ak.insight.util.Utilities
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.io.File
import java.lang.StringBuilder


class SpContentFragment : AkFragment() {
    companion object {
        private const val TAG = "SpContentFragment"

        fun createFragment(path: String): AkFragment {
            val fragment = SpContentFragment()
            fragment.mSpFile = File(path)
            return fragment
        }
    }

    private lateinit var mSpFile: File
    private lateinit var mSpName: String

    override fun getLayoutResId(): Int {
        return R.layout.fragment_sp_content
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        mSpName = mSpFile.name.substringBeforeLast(".")
        topBar.setTitle(mSpName)
        val tvInfo: TextView = root.findViewById(R.id.tv_info)
        val sp = TargetApp.targetContext.getSharedPreferences(mSpName, Context.MODE_PRIVATE)
        val map = sp.all

        val infoBuilder = StringBuilder()
        infoBuilder.append("路径：${mSpFile.absolutePath}\n")
        infoBuilder.append("数量：${map.size}\n")
        infoBuilder.append("占用空间：${Utilities.formatFileLength(mSpFile.length())}")
        tvInfo.text = infoBuilder.toString()

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        val tvState = root.findViewById<TextView>(R.id.tv_state)
        val list: List<MutableMap.MutableEntry<String, out Any?>> =
            map.entries.toList().sortedWith(Comparator<MutableMap.MutableEntry<String, out Any?>>
            { o1, o2 -> o1.key.compareTo(o2.key) })

        if (list.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvState.visibility = View.GONE

            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = ContentAdapter()
            adapter.addData(list)
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener { adapter, view, position ->
                val entry = adapter.data[position] as MutableMap.MutableEntry<String, out Any?>
                startFragment(SpEditFragment.createFragment(mSpName, entry.key))
            }
        }
    }

    class ContentAdapter :
        BaseQuickAdapter<MutableMap.MutableEntry<String, out Any?>, BaseViewHolder>(R.layout.layout_sp_content_item) {
        override fun convert(
            holder: BaseViewHolder,
            item: MutableMap.MutableEntry<String, out Any?>
        ) {

            val txtKey = item.key
            val txtValue = item.value.toString()
            holder.setText(R.id.tv_key, "${holder.adapterPosition + 1}.${txtKey}")
            val showArrow = txtValue.length > 15 && txtKey.length + txtValue.length > 40
            if (showArrow) {
                holder.setText(R.id.tv_value, ">")
            } else {
                holder.setText(R.id.tv_value, txtValue)
            }

        }
    }
}