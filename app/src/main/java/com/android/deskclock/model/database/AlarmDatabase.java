package com.android.deskclock.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDatabase extends SQLiteOpenHelper {

    private static final String DatabaseName = "AlarmDatabase.db";
    private static final int version = 1;

    AlarmDatabase(Context context) {
        this(context, DatabaseName, null, version);
    }

    AlarmDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AlarmDatabaseEntity.TABLE_NAME + "(" +
                AlarmDatabaseEntity.COLUMN_ID + " text Primary Key, " +
                AlarmDatabaseEntity.COLUMN_LABEL + " text, " +
                AlarmDatabaseEntity.COLUMN_REMINDHOUR + " integer, " +
                AlarmDatabaseEntity.COLUMN_REMINDMINUTE + " integer, " +
                AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK + " integer, " +
                AlarmDatabaseEntity.COLUMN_AUDIO + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // no update

    }

    protected static final class AlarmDatabaseEntity {
        static final String TABLE_NAME = "SHADOW_ALARM";
        static final String COLUMN_ID = "id";
        static final String COLUMN_LABEL = "label";
        static final String COLUMN_REMINDHOUR = "remindHours";
        static final String COLUMN_REMINDMINUTE = "remindMinutes";
        static final String COLUMN_REMINDDAYSINWEEK = "remindDaysInWeek";
        static final String COLUMN_AUDIO = "alarmAudio";
    }
}
