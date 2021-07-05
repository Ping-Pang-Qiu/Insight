package com.ak.insight.fragment


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ak.framework.app.AkFragment
import com.ak.framework.util.AkToast
import com.ak.framework.util.ext.setOnDebounceClickListener
import com.ak.framework.util.io.AkFileUtils
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.FileModel
import com.ak.insight.util.Utilities
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*
import java.io.File
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class FileListFragment : AkFragment() {
    companion object {
        private const val TAG = "FileListFragment"

        private val fileDataFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")

        fun createFragment(name: String, path: String): AkFragment {
            val fragment = FileListFragment()
            fragment.mName = name
            fragment.mRootDir = File(path)
            return fragment
        }
    }

    private lateinit var mName: String
    private lateinit var mRootDir: File
    private lateinit var mCurrentDir: File

    private lateinit var mTvParentDir: TextView
    private lateinit var mTvInfo: TextView
    private lateinit var mTvState: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FileListAdapter

    private var mMainScope = MainScope()
    private var mLastJob: Job? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_file_list
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle(mName)

        mTvParentDir = root.findViewById(R.id.tv_parent_dir)
        mTvParentDir.setOnDebounceClickListener(View.OnClickListener { onClickParentDir() })
        mTvInfo = root.findViewById(R.id.tv_info)
        mTvState = root.findViewById(R.id.tv_state)
        mRecyclerView = root.findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = FileListAdapter()
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val data = adapter.data[position] as FileModel.FileSizeData
            onClickFile(data.file)
        }

        mRecyclerView.adapter = mAdapter
        selectDirectory(mRootDir)
    }


    private fun selectDirectory(dir: File) {
        mLastJob?.cancel()
        mCurrentDir = dir
        mLastJob = mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                FileModel.getDirSizeData(dir)
            }
            fillData(data)
        }
    }

    private fun onClickFile(file: File) {
        if (file.isDirectory) {
            selectDirectory(file)
            return
        }

        if (AkFileUtils.isImageFile(file)) {
            // 图片
        } else if (AkFileUtils.isVideoFile(file)) {
            // 视频
        } else {
            if (file.length() < 200 * 1024) {
                startFragment(FileTextFragment.createFragment(file.absolutePath))
            } else {
                AkToast.show("文本大于200k，不打开!")
            }

        }
    }

    private fun onClickParentDir() {
        if (mCurrentDir != mRootDir) {
            selectDirectory(mCurrentDir.parentFile)
        }
    }

    private fun fillData(data: FileModel.DirSizeData) {
        if (mRootDir == data.dirData.file) {
            mTvParentDir.visibility = View.GONE
        } else {
            mTvParentDir.visibility = View.VISIBLE
        }

        val builder = StringBuilder()
        builder.append("项：${data.list.size}")
        builder.append("，文件：${data.dirData.count}")
        builder.append("，占用空间：${Utilities.formatFileLength(data.dirData.totalLength)}")
        builder.append("\n路径：${data.dirData.file.absolutePath}")
        mTvInfo.text = builder.toString()

        if (data.list.isEmpty()) {
            mTvState.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
        } else {
            mAdapter.setNewInstance(data.list.toMutableList())
            mTvState.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }
    }

    class FileListAdapter :
        BaseQuickAdapter<FileModel.FileSizeData, BaseViewHolder>(R.layout.layout_file_list_item) {
        override fun convert(holder: BaseViewHolder, data: FileModel.FileSizeData) {
            val file = data.file
            val title = "${holder.adapterPosition + 1}.${data.file.name}"
            val summary = if (file.isDirectory) {
                "${fileDataFormat.format(Date(data.file.lastModified()))}" +
                        " - 文件${data.count}" +
                        " - ${Utilities.formatFileLength(data.totalLength)}"
            } else {
                "${fileDataFormat.format(Date(data.file.lastModified()))}" +
                        " - ${Utilities.formatFileLength(data.totalLength)}"
            }

            val iconResId = if (data.file.isDirectory) {
                R.drawable.ic_file_folder
            } else {
                R.drawable.ic_file_common
            }
            holder.setText(R.id.tv_name, title)
            holder.setText(R.id.tv_summary, summary)
            holder.setImageResource(R.id.icon, iconResId)
        }
    }
}