package com.ak.insight.fragment

import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import com.ak.framework.app.AkFragment
import com.ak.framework.util.AkToast
import com.ak.framework.util.ext.getAppDataDir
import com.ak.framework.util.ext.getAppExternalDir
import com.ak.framework.util.ext.isAppInstalled
import com.ak.framework.util.ext.setOnDebounceClickListener
import com.ak.framework.util.os.AkBuildUtils
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.TargetApp
import com.ak.insight.R
import com.ak.insight.model.DbModel
import com.ak.insight.model.FileModel
import com.ak.insight.model.SpModel
import com.ak.insight.model.ProcessModel
import com.ak.insight.util.Utilities
import kotlinx.coroutines.*
import java.io.File
import java.lang.StringBuilder


class HomeFragment : AkFragment(), View.OnClickListener {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private var mMainScope = MainScope()
    private lateinit var mTvRefresh: TextView
    private lateinit var mTvError: TextView
    private lateinit var mLayoutContent: View
    private lateinit var mLayoutProcess: View
    private lateinit var mLayoutInternalStorage: View
    private lateinit var mLayoutExternalStorage: View
    private lateinit var mLayoutSp: View
    private lateinit var mLayoutDb: View

    private lateinit var mInternalStorageDir: File
    private lateinit var mExternalStorageDir: File

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle(TargetApp.TARGET_PACKAGE_NAME)
        mTvError = root.findViewById(R.id.tv_error)
        mTvRefresh = root.findViewById(R.id.tv_refresh)
        mTvRefresh.setOnDebounceClickListener(this)
        mLayoutContent = root.findViewById<View>(R.id.layout_content)

        // 进程信息
        mLayoutProcess = root.findViewById(R.id.layout_process)
        mLayoutProcess.findViewById<TextView>(R.id.tv_title)
            .setText(R.string.target_process_info)
        mLayoutProcess.findViewById<View>(R.id.iv_arrow).visibility = View.GONE

        // 内部存储
        mLayoutInternalStorage = root.findViewById(R.id.layout_internal_storage)
        mLayoutInternalStorage.setOnDebounceClickListener(this)
        mLayoutInternalStorage.findViewById<TextView>(R.id.tv_title)
            .setText(R.string.internal_storage)

        // 外部存储
        mLayoutExternalStorage = root.findViewById(R.id.layout_external_storage)
        mLayoutExternalStorage.setOnDebounceClickListener(this)
        mLayoutExternalStorage.findViewById<TextView>(R.id.tv_title)
            .setText(R.string.external_storage)

        // SP
        mLayoutSp = root.findViewById(R.id.layout_sp)
        mLayoutSp.setOnDebounceClickListener(this)
        mLayoutSp.findViewById<TextView>(R.id.tv_title).setText(R.string.shared_preference)

        // 数据库
        mLayoutDb = root.findViewById(R.id.layout_db)
        mLayoutDb.setOnDebounceClickListener(this)
        mLayoutDb.findViewById<TextView>(R.id.tv_title).setText(R.string.database)

        refreshData(false)
    }

    private fun refreshData(remind: Boolean) {
        val context = context!!
        // 目标应用未安装
        if (!context.isAppInstalled(TargetApp.TARGET_PACKAGE_NAME)) {
            mTvError.text =
                getString(R.string.error_target_app_not_installed, TargetApp.TARGET_PACKAGE_NAME)
            mTvError.visibility = View.VISIBLE
            mLayoutContent.visibility = View.GONE
            return
        }

        // 读不到目标应用文件，请检查
        mInternalStorageDir = TargetApp.targetContext.getAppDataDir()
        if (mInternalStorageDir.list() == null) {
            mTvError.setText(R.string.error_target_app_files_unavailable)
            mTvError.visibility = View.VISIBLE
            mLayoutContent.visibility = View.GONE
            return
        }

        mTvError.visibility = View.GONE
        mLayoutContent.visibility = View.VISIBLE

        // 进程信息
        val targetPid = TargetApp.getTargetPid()
        if (targetPid == -1) {
            mLayoutProcess.findViewById<TextView>(R.id.tv_summary).text =
                getString(R.string.error_target_app_not_running, TargetApp.TARGET_PACKAGE_NAME)
        } else {
            val builder = StringBuilder()
            builder.append("线程数量：${ProcessModel.getThreadCount(targetPid)}\n")
            builder.append("文件描述符数量：${ProcessModel.getFdCount(targetPid)}\n")
            val memoryInfo = ProcessModel.getMemoryInfo(targetPid)
            val totalPss = Utilities.formatMemorySize(memoryInfo.totalPss)
            builder.append("内存占用(total pss)：${totalPss}")
            mLayoutProcess.findViewById<TextView>(R.id.tv_summary).text = builder.toString()
        }

        // 内部存储
        mMainScope.launch {
            val info = withContext(Dispatchers.IO) {
                FileModel.getFileSizeData(mInternalStorageDir)
            }
            fillInternalStorage(info)
        }

        // 外部存储
        val externalEnable = !AkBuildUtils.AT_LEAST_23 ||
                TargetApp.targetContext.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (externalEnable) {
            mExternalStorageDir = TargetApp.targetContext.getAppExternalDir()
            mLayoutExternalStorage.visibility = View.VISIBLE
            mMainScope.launch {
                val data = withContext(Dispatchers.IO) {
                    FileModel.getFileSizeData(mExternalStorageDir)
                }
                fillExternalStorage(data)
            }
        } else {
            mLayoutExternalStorage.visibility = View.GONE
        }

        // sp
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                SpModel.loadSpListData()
            }
            fillSpListData(data)
        }

        // 数据库
        mMainScope.launch {
            val data = withContext(Dispatchers.IO) {
                DbModel.loadDbListData()
            }
            fillDbListData(data)
        }

        if (remind) {
            AkToast.show(R.string.state_refresh_success)
        }
    }

    private fun fillDbListData(data: DbModel.DbListData) {
        val tvSummary = mLayoutDb.findViewById<TextView>(R.id.tv_summary)

        val builder = StringBuilder()
        builder.append("路径：${data.dir.absolutePath}\n")
        builder.append("数据库：${data.files.size}\n")
        builder.append("占用空间：${Utilities.formatFileLength(data.totalLength)}")
        tvSummary.text = builder.toString()
    }

    private fun fillSpListData(data: SpModel.SpListData) {
        val tvSummary = mLayoutSp.findViewById<TextView>(R.id.tv_summary)

        val builder = StringBuilder()
        builder.append("路径：${data.dir.absolutePath}\n")
        builder.append("文件数：${data.files.size}\n")
        builder.append("占用空间：${Utilities.formatFileLength(data.totalLength)}")
        tvSummary.text = builder.toString()
    }

    private fun fillInternalStorage(data: FileModel.FileSizeData) {
        val tvSummary = mLayoutInternalStorage.findViewById<TextView>(R.id.tv_summary)
        val builder = StringBuilder()
        builder.append("路径：${data.file.absolutePath}\n")
        builder.append("文件数：${data.count}\n")
        builder.append("占用空间：${Utilities.formatFileLength(data.totalLength)}")
        tvSummary.text = builder.toString()
    }

    private fun fillExternalStorage(data: FileModel.FileSizeData) {
        val tvSummary = mLayoutExternalStorage.findViewById<TextView>(R.id.tv_summary)
        val builder = StringBuilder()
        builder.append("路径：${data.file.absolutePath}\n")
        builder.append("文件数：${data.count}\n")
        builder.append("占用空间：${Utilities.formatFileLength(data.totalLength)}")
        tvSummary.text = builder.toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_sp -> {
                startFragment(SpListFragment())
            }
            R.id.layout_db -> {
                startFragment(DbListFragment())
            }
            R.id.layout_internal_storage -> {
                val name = getString(R.string.internal_storage)
                startFragment(
                    FileListFragment.createFragment(
                        name,
                        mInternalStorageDir.absolutePath
                    )
                )
            }
            R.id.layout_external_storage -> {
                val name = getString(R.string.external_storage)
                startFragment(
                    FileListFragment.createFragment(
                        name,
                        mExternalStorageDir.absolutePath
                    )
                )
            }
            R.id.tv_refresh -> {
                refreshData(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMainScope.cancel()
    }

}