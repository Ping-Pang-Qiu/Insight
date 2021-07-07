package com.ak.insight.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.insight.R
import com.ak.insight.model.DbModel
import com.ak.insight.util.Utilities
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*
import java.io.File


class DbListFragment : AkFragment() {
    private companion object {
        private const val TAG = "DbListFragment"
    }

    private var mMainScope = MainScope()

    override fun getLayoutResId(): Int {
        return R.layout.fragment_db_list
    }

    override fun onViewCreated(root: View) {
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                DbModel.loadDbListData()
            }
            fillData(data)
        }
    }

    /**
     * 填充数据
     */
    private fun fillData(data: DbModel.DbListData) {
        val rootView = view ?: return

        val tvInfo: TextView = rootView.findViewById(R.id.tv_info)
        tvInfo.text = "数据库：${data.files.size}，占用空间：${Utilities.formatFileLength(data.totalLength)}"

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = DbFilesAdapter()
        adapter.addData(data.files)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val dbFile = adapter.data[position] as File
            startFragment(DbTableListFragment.createFragment(dbFile.absolutePath))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMainScope.cancel()
    }

    class DbFilesAdapter : BaseQuickAdapter<File, BaseViewHolder>(R.layout.layout_db_list_item) {
        override fun convert(holder: BaseViewHolder, file: File) {
            holder.setText(
                    R.id.tv_name,
                    "${holder.adapterPosition + 1}.${file.name.substringBeforeLast(".")}"
            )

            val tvSize = holder.getView<TextView>(R.id.tv_size)
            tvSize.text = Utilities.formatFileLength(file.length())
        }
    }
}