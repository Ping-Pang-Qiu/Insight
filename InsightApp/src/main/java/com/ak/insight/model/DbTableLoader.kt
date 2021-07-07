package com.ak.insight.model

import android.database.Cursor
import com.ak.framework.util.io.AkIOUtils
import java.lang.Exception

class DbTableLoader(
    private val mSQLiteModel: DbSQLiteModel,
    private val mTabInfo: DbSQLiteModel.TableInfo
) {

    private companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }

    data class RowData(val list: List<String>)

    data class PageData(val list: List<RowData>, val reachEnd: Boolean)

    private var mCursor: Cursor? = null

    private var mIsLoading: Boolean = false
    private var mReachEnd: Boolean = false

    fun loadFirst(): PageData? {
        if (mIsLoading) {
            return null
        }

        mIsLoading = true

        try {
            AkIOUtils.closeSilently(mCursor)
            val cursor = mSQLiteModel.query(mTabInfo.name, null, null, null)
            mCursor = cursor
            return loadPageData()
        } catch (e: Exception) {

        } finally {
            mIsLoading = false
        }

        return null
    }

    fun loadMore(): PageData? {
        try {
            return loadPageData()
        } catch (e: Exception) {

        }
        return null
    }

    private fun loadPageData(): PageData? {
        val cursor = mCursor
        if (cursor == null || cursor.count == 0) {
            return null
        }

        val colCount = cursor.columnCount
        val rowList = mutableListOf<RowData>()
        var reachEnd = true
        while (cursor.moveToNext()) {
            val values = mutableListOf<String>()
            (0 until colCount).forEach {
                values.add(mSQLiteModel.getColumnValue(cursor, it).toString())
            }

            rowList.add(RowData(values))
            if (rowList.size == DEFAULT_PAGE_SIZE) {
                reachEnd = false
                break
            }
        }

        if (reachEnd) {
            AkIOUtils.closeSilently(mCursor)
            mCursor = null
        }

        mReachEnd = reachEnd
        return PageData(rowList, reachEnd)
    }

    fun isReachEnd(): Boolean {
        return mReachEnd
    }

    fun release() {
        AkIOUtils.closeSilently(mCursor)
        mCursor = null
    }
}