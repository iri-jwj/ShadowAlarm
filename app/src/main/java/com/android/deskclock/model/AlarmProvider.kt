package com.android.deskclock.model

import android.content.ContentProvider
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.android.deskclock.model.database.AlarmDatabase

class AlarmProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.android.deskclock.provider"
        const val PATH = "shadowAlarm"
        private const val CODE = 101

        fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(AUTHORITY, PATH, CODE)
            return matcher
        }
    }

    private lateinit var mDatabase: AlarmDatabase
    private var writableDb: SQLiteDatabase? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        synchronized(mDatabase) {
            if (isMatchedUri(p0)) {
                try {
                    writableDb = mDatabase.writableDatabase
                    writableDb?.beginTransaction()
                    writableDb?.insert(AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, null, p1)
                    writableDb?.setTransactionSuccessful()
                    writableDb?.endTransaction()
                } finally {
                    writableDb?.close()
                }
            }
            return null
        }
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        synchronized(mDatabase) {
            if (isMatchedUri(p0)) {
                try {
                    writableDb = mDatabase.readableDatabase
                    writableDb?.beginTransaction()
                    val cursor = writableDb?.query(
                        AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR + " ," + AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE + " ASC"
                    )
                    writableDb?.setTransactionSuccessful()

                    val result: Cursor?
                    if (cursor?.count != 0) {
                        result = cursor!!
                    } else {
                        result = null
                        cursor.close()
                    }
                    writableDb?.endTransaction()

                    return result
                } finally {
                    writableDb?.close()
                }
            } else {
            return null
            }
        }
    }

    override fun onCreate(): Boolean {
        mDatabase = AlarmDatabase.getInstance(context)
        return true
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        synchronized(mDatabase) {
            var result: Int? = 0
            if (isMatchedUri(p0)) {
                try {
                    writableDb = mDatabase.writableDatabase
                    writableDb?.beginTransaction()
                    result = writableDb?.update(
                        AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, p1, p2,
                        p3
                    )
                    writableDb?.setTransactionSuccessful()
                    writableDb?.endTransaction()

                } finally {
                    writableDb?.close()
                }
            }
            return result!!
        }
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        synchronized(mDatabase) {
            var result: Int? = 0
            if (isMatchedUri(p0)) {
                try {
                    writableDb = mDatabase.writableDatabase
                    writableDb?.beginTransaction()
                    result = writableDb?.delete(
                        AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME,
                        p1,
                        p2
                    )

                    if (result != 0) {
                        Log.d(TAG, "delete alarm id =$p1 success")
                    }
                    writableDb?.setTransactionSuccessful()
                    writableDb?.endTransaction()

                } finally {
                    writableDb?.close()
                }
            }
            return result!!
        }
    }

    private fun isMatchedUri(uri: Uri): Boolean {
        return buildUriMatcher().match(uri) == CODE
    }

    override fun getType(p0: Uri): String? {
        return null
    }


}