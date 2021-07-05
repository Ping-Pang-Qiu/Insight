package com.ak.framework.util.io

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.ak.framework.app.AkApplication
import com.ak.framework.util.os.AkBuildUtils


object AkUriUtils {
    fun parsePathFromUri(uri: Uri): String? {
        val context = AkApplication.appContext
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_FILE.equals(scheme, true)) {
            // file
            return uri.path
        }

        val authority = uri.authority
        if (AkBuildUtils.AT_LEAST_19 && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.localstorage.documents" == authority) {
                return DocumentsContract.getDocumentId(uri)
            } else if ("com.android.externalstorage.documents" == authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == authority) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                if (contentUri == null) {
                    return null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(
                    context,
                    contentUri,
                    selection,
                    selectionArgs
                )
            }
        }

        if (ContentResolver.SCHEME_CONTENT.equals(scheme, true)) {
            if ("com.google.android.apps.photos.content" == authority) {
                return uri.lastPathSegment
            }
            return getDataColumn(context, uri, null, null)
        }

        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return ""
    }
}