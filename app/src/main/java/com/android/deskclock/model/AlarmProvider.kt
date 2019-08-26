package com.android.deskclock.model

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.android.deskclock.model.database.AlarmDatabaseHelper

class AlarmProvider :ContentProvider {
    companion object{
        const val AUTHORITY = "com.android.deskclock"
        const val PATH = "shadowAlarm"
        const val CODE = 101

        fun buildUriMatcher():UriMatcher{
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(AUTHORITY, PATH,CODE)
            return matcher
        }
    }

    private lateinit var mDatabaseHelper: AlarmDatabaseHelper

    constructor(context:Context):super(){
        mDatabaseHelper = AlarmDatabaseHelper(context)
    }


    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(p0: Uri): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}