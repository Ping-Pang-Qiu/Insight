package com.ak.insight.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.DbSQLiteModel
import com.ak.insight.model.DbTableLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbRowContentFragment : AkFragment() {
    companion object {
        private const val TAG = "DbRowContentFragment"

        fun createFragment(
            sqLiteModel: DbSQLiteModel,
            tabInfo: DbSQLiteModel.TableInfo,
            rowData: DbTableLoader.RowData
        ): AkFragment {
            val fragment = DbRowContentFragment()
            fragment.mSQLiteModel = sqLiteModel
            fragment.mTabInfo = tabInfo
            fragment.mRowData = rowData
            return fragment
        }
    }

    private var mMainScope = MainScope()
    private lateinit var mSQLiteModel: DbSQLiteModel
    private lateinit var mTabInfo: DbSQLiteModel.TableInfo
    private lateinit var mRowData: DbTableLoader.RowData

    override fun getLayoutResId(): Int {
        return R.layout.fragment_db_row_content
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle("详细数据")

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = DbTablesAdapter()
        adapter.addData(mTabInfo.columnInfoList)
        recyclerView.adapter = adapter
    }


    inner class DbTablesAdapter :
        BaseQuickAdapter<DbSQLiteModel.ColumnInfo, BaseViewHolder>(R.layout.layout_db_row_value_item) {
        override fun convert(holder: BaseViewHolder, info: DbSQLiteModel.ColumnInfo) {
            val title = if (info.type.isNullOrEmpty()) {
                info.name
            } else {
                "${info.name} (${info.type})"
            }
            holder.setText(R.id.tv_key, title)
            holder.setText(R.id.tv_value, mRowData.list[holder.adapterPosition])
        }
    }
}