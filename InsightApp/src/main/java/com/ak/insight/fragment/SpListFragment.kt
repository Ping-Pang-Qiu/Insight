package com.ak.insight.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.SpModel
import com.ak.insight.util.Utilities
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*
import java.io.File


class SpListFragment : AkFragment() {
    private companion object {
        private const val TAG = "SpListFragment"
    }

    private var mMainScope = MainScope()

    override fun getLayoutResId(): Int {
        return R.layout.fragment_sp_list
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle("SharedPreference")
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                SpModel.loadSpListData()
            }
            fillData(data)
        }
    }

    /**
     * 填充数据
     */
    private fun fillData(data: SpModel.SpListData) {
        val rootView = view ?: return

        val tvInfo: TextView = rootView.findViewById(R.id.tv_info)
        tvInfo.text = "文件：${data.files.size}，占用空间：${Utilities.formatFileLength(data.totalLength)}"

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = SpFilesAdapter()
        adapter.addData(data.files)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val file = adapter.data[position] as File
            startFragment(SpContentFragment.createFragment(file.absolutePath))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mMainScope.cancel()
    }

    class SpFilesAdapter : BaseQuickAdapter<File, BaseViewHolder>(R.layout.layout_sp_list_item) {
        override fun convert(holder: BaseViewHolder, file: File) {
            holder.setText(
                R.id.tv_name,
                "${holder.adapterPosition + 1}.${file.name.substringBeforeLast(".")}"
            )

            val tvSize = holder.getView<TextView>(R.id.tv_size)
            tvSize.text = Utilities.formatFileLength(file.length())
            if (file.length() > 50 * 1024) {
                tvSize.setTextColor(context.resources.getColor(R.color.ak_color_red))
            } else {
                tvSize.setTextColor(context.resources.getColor(R.color.ak_color_txt_summary_black))
            }
        }
    }
}