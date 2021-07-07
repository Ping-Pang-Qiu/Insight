package com.ak.insight.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ak.framework.util.AkLog
import com.ak.framework.util.io.AkIOUtils
import java.io.File

class DbSQLiteModel(private val mDbFile: File) {

    data class ColumnInfo(val name: String, val type: String)

    data class TableInfo(val name: String, val size: Int, val columnInfoList: List<ColumnInfo>)

    companion object {
        private const val TAG = "DbSQLiteModel"

        fun create(path: String): DbSQLiteModel {
            return DbSQLiteModel(File(path))
        }
    }

    private val mName: String = mDbFile.name.substringBeforeLast(".")
    private var mSQLiteDatabase: SQLiteDatabase? = null

    fun getName(): String {
        return mName
    }

    fun getFile(): File {
        return mDbFile
    }

    fun query(
        table: String,
        columns: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Cursor? {
        val db = openDb()
        return db.query(table, columns, selection, selectionArgs, null, null, null)
    }


    fun queryTableInfoList(): List<TableInfo> {
        val result = mutableListOf<TableInfo>()
        var cursor: Cursor? = null
        try {
            cursor = query(
                "sqlite_master",
                arrayOf("name"),
                "type=?",
                arrayOf("table")
            )
            if (cursor == null || cursor.count == 0) {
                return result
            }

            while (cursor.moveToNext()) {
                val name = cursor.getString(0)
                val size = queryTableSize(name)
                val columnInfoList = queryColumnInfoList(name)
                result.add(TableInfo(name, size, columnInfoList))
            }
        } catch (e: Exception) {
            AkLog.e(e)
        } finally {
            AkIOUtils.closeSilently(cursor)
        }

        return result
    }

    private fun queryTableSize(tableName: String): Int {
        var cursor: Cursor? = null
        try {
            cursor = query(
                tableName,
                arrayOf("count(*) as totalSize"),
                null,
                null
            )
            if (cursor == null || cursor.count == 0) {
                return 0
            }

            cursor.moveToFirst()
            return  cursor.getInt(0)
        } catch (e: Exception) {
            AkLog.e(e)
        } finally {
            AkIOUtils.closeSilently(cursor)
        }

        return 0
    }

    private fun queryColumnInfoList(tableName: String): List<ColumnInfo> {
        val result = mutableListOf<ColumnInfo>()
        var cursor: Cursor? = null
        try {
            val db = openDb()
            val sql = "PRAGMA table_info('${tableName}')"
            cursor = db.rawQuery(sql, null)
            if (cursor == null || cursor.count == 0) {
                return result
            }

            while (cursor.moveToNext()) {
                result.add(ColumnInfo(cursor.getString(1), cursor.getString(2)))
            }
        } catch (e: Exception) {
            AkLog.e(e)
        } finally {
            AkIOUtils.closeSilently(cursor)
        }

        return result
    }

    fun getColumnValue(cursor: Cursor, columnIndex: Int): Any? {
        val type = cursor.getType(columnIndex)
        var obj: Any?
        obj = when (type) {
            Cursor.FIELD_TYPE_NULL -> null
            Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(columnIndex)
            Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(columnIndex)
            Cursor.FIELD_TYPE_STRING -> cursor.getString(columnIndex)
            Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(columnIndex)
            else -> null
        }

        return obj
    }

    fun release() {
        closeDb()
    }


    private fun openDb(): SQLiteDatabase {
        var db = mSQLiteDatabase
        if (db == null || !db.isOpen) {
            db = SQLiteDatabase.openDatabase(
                mDbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            mSQLiteDatabase = db
        }
        return db!!
    }

    private fun closeDb() {
        var db = mSQLiteDatabase
        if (db != null && db.isOpen) {
            db.close()
        }
        mSQLiteDatabase = null
    }
}