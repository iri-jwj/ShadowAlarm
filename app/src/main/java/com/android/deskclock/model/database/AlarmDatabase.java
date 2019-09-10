package com.android.deskclock.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDatabase extends SQLiteOpenHelper {

    private static final String DatabaseName = "AlarmDatabase.db";
    private static final int version = 1;

    private static AlarmDatabase instance = null;

    public static AlarmDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmDatabase(context);
        }
        return instance;
    }

    private AlarmDatabase(Context context) {
        super(context, DatabaseName, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AlarmDatabaseEntity.TABLE_NAME + "(" +
                AlarmDatabaseEntity.COLUMN_ID + " text Primary Key, " +
                AlarmDatabaseEntity.COLUMN_LABEL + " text, " +
                AlarmDatabaseEntity.COLUMN_REMINDHOUR + " integer, " +
                AlarmDatabaseEntity.COLUMN_REMINDMINUTE + " integer, " +
                AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK + " integer ," +
                AlarmDatabaseEntity.COLUMN_REMIND_ACTION + " integer ," +
                AlarmDatabaseEntity.COLUMN_REMIND_AUDIO + " text, " +
                AlarmDatabaseEntity.COLUMN_ENABLED + " bool)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // no update
    }

    public static final class AlarmDatabaseEntity {
        public static final String TABLE_NAME = "SHADOW_ALARM";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_REMINDHOUR = "remindHours";
        public static final String COLUMN_REMINDMINUTE = "remindMinutes";
        public static final String COLUMN_REMINDDAYSINWEEK = "remindDaysInWeek";
        public static final String COLUMN_REMIND_ACTION = "remindAction";
        public static final String COLUMN_REMIND_AUDIO = "remindAudio";
        public static final String COLUMN_ENABLED = "isEnabled";
    }
}
