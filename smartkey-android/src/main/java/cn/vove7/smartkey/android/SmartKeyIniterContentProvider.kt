package cn.vove7.smartkey.android

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * # SmartKeyIniterContentProvider
 *
 * @author Vove
 * 2019/10/24
 */
class SmartKeyIniterContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        AndroidSettings.init(context!!.applicationContext)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun getType(uri: Uri): String? = null
}