package com.ak.insight.fragment

import android.view.View
import android.widget.TextView
import com.ak.framework.app.AkFragment
import com.ak.framework.util.ext.setOnDebounceClickListener
import com.ak.framework.widget.topbar.AkTopBar
import com.ak.insight.R
import com.ak.insight.model.FileModel
import com.ak.insight.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.StringBuilder

class FileTextFragment : AkFragment() {

    companion object {
        private const val TAG = "FileTextFragment"

        fun createFragment(path: String): AkFragment {
            val fragment = FileTextFragment()
            fragment.mFile = File(path)
            return fragment
        }
    }

    private lateinit var mFile: File
    private var mMainScope = MainScope()

    private lateinit var mTvInfo: TextView
    private lateinit var mTvContent: TextView

    override fun getLayoutResId(): Int {
        return R.layout.fragment_file_text
    }

    override fun onViewCreated(root: View) {
        val topBar: AkTopBar = root.findViewById(R.id.title_bar)
        topBar.setTitle(mFile.name)

        mTvInfo = root.findViewById(R.id.tv_info)
        mTvContent = root.findViewById(R.id.tv_content)
        val builder = StringBuilder()
        builder.append("占用空间：${Utilities.formatFileLength(mFile.length())}")
        builder.append("\n路径：${mFile.absolutePath}")
        mTvInfo.text = builder.toString()

        mMainScope.launch {
            val content = withContext(Dispatchers.IO) {
                mFile.readText()
            }
            mTvContent.text = content
        }
    }
}