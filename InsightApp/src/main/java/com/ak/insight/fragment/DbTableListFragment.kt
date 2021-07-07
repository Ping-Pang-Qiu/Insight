package com.ak.insight.fragment

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.DbSQLiteModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbTableListFragment : AkFragment() {
    companion object {
        private const val TAG = "DbTableListFragment"

        fun createFragment(path: String): AkFragment {
            val fragment = DbTableListFragment()
            fragment.mSQLiteModel = DbSQLiteModel.create(path)
            return fragment
        }
    }

    private var mMainScope = MainScope()
    private lateinit var mSQLiteModel: DbSQLiteModel

    override fun getLayoutResId(): Int {
        return R.layout.fragment_db_table_list
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle("数据库${mSQLiteModel.getName()}")

        mMainScope.launch {
            val list = withContext(Dispatchers.IO) {
                mSQLiteModel.queryTableInfoList()
            }
            fillData(list)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mSQLiteModel.release()
    }

    /**
     * 填充数据
     */
    private fun fillData(list: List<DbSQLiteModel.TableInfo>) {
        val rootView = view ?: return

        val tvInfo: TextView = rootView.findViewById(R.id.tv_info)
        tvInfo.text = "表：${list.size}\n路径：${mSQLiteModel.getFile().absolutePath}"

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = DbTablesAdapter()
        adapter.addData(list)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            val info = adapter.data[position] as DbSQLiteModel.TableInfo
            startFragment(DbTableContentFragment.createFragment(mSQLiteModel, info))
        }
    }

    class DbTablesAdapter : BaseQuickAdapter<DbSQLiteModel.TableInfo, BaseViewHolder>(R.layout.layout_db_table_list_item) {
        override fun convert(holder: BaseViewHolder, info: DbSQLiteModel.TableInfo) {
            holder.setText(R.id.tv_name, info.name)
            holder.setText(R.id.tv_size, "${info.size}")
        }
    }
}