package com.ak.insight.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.util.AkLog
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.DbSQLiteModel
import com.ak.insight.model.DbTableLoader
import com.ak.insight.widget.DbTableRowLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbTableContentFragment : AkFragment() {
    companion object {
        private const val TAG = "DbTableContentFragment"

        fun createFragment(model: DbSQLiteModel, info: DbSQLiteModel.TableInfo): AkFragment {
            val fragment = DbTableContentFragment()
            fragment.mSQLiteModel = model
            fragment.mTableInfo = info
            return fragment
        }
    }

    private var mMainScope = MainScope()
    private lateinit var mSQLiteModel: DbSQLiteModel
    private lateinit var mTableInfo: DbSQLiteModel.TableInfo
    private lateinit var mLoader: DbTableLoader

    private lateinit var mTvInfo: TextView
    private lateinit var mTitleRowLayout: DbTableRowLayout
    private lateinit var mAdapter: DbRowsAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun getLayoutResId(): Int {
        return R.layout.fragment_db_table_content
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle("表${mTableInfo.name}")

        mTvInfo = root.findViewById(R.id.tv_info)


        val columnList = mTableInfo.columnInfoList
        val titleRowLayout: DbTableRowLayout = root.findViewById(R.id.db_title_row)
        titleRowLayout.fillColumnInfoAsTitle(columnList)
        columnList.forEachIndexed { index, info ->
            titleRowLayout.setTextOfIndex(index, info.name)
        }
        mTitleRowLayout = titleRowLayout
        mLoader = DbTableLoader(mSQLiteModel, mTableInfo)
        mLayoutManager = LinearLayoutManager(context)
        mAdapter = DbRowsAdapter()
        mAdapter.loadMoreModule.apply {
            setOnLoadMoreListener {
                loadMore()
            }
            preLoadNumber = 10
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val row = adapter.data[position] as DbTableLoader.RowData
            startFragment(DbRowContentFragment.createFragment(mSQLiteModel, mTableInfo, row))
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                refreshInfo()
            }
        })

        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
            }

            override fun onChildViewAttachedToWindow(view: View) {
                refreshInfo()
            }

        })

        recyclerView.adapter = mAdapter
        loadFirst()
        refreshInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoader.release()
    }

    private fun refreshInfo() {
        val start = mLayoutManager.findFirstVisibleItemPosition() + 1
        val end = mLayoutManager.findLastVisibleItemPosition() + 1

        if (end > 0) {
            mTvInfo.text = "数据：${mTableInfo.size}, 当前展示：$start - $end"
        } else {
            mTvInfo.text = "数据：${mTableInfo.size}"
        }
    }

    private fun loadFirst() {
        AkLog.t(TAG).d("loadFirst")
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                mLoader.loadFirst()
            }

            if (data != null) {
                fillPageData(data)
            }
        }
    }

    private fun loadMore() {
        AkLog.t(TAG).d("loadMore")
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                mLoader.loadMore()
            }


            if (data != null) {
                fillPageData(data)
            }
        }
    }

    private fun fillPageData(pageData: DbTableLoader.PageData) {
        mAdapter.addData(pageData.list)

        if (pageData.reachEnd) {
            mAdapter.loadMoreModule.loadMoreEnd(true)
        } else {
            mAdapter.loadMoreModule.loadMoreComplete()
        }
    }

    inner class DbRowsAdapter :
        BaseQuickAdapter<DbTableLoader.RowData, BaseViewHolder>(R.layout.layout_db_table_row),
        LoadMoreModule {

        override fun convert(holder: BaseViewHolder, data: DbTableLoader.RowData) {
            val layout = holder.itemView as DbTableRowLayout
            data.list.forEachIndexed() { index, str ->
                layout.setTextOfIndex(index, str)
            }
        }

        override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val holder = super.onCreateDefViewHolder(parent, viewType)
            (holder.itemView as DbTableRowLayout).cloneChildrenWithWidth(mTitleRowLayout)
            return holder
        }

    }
}