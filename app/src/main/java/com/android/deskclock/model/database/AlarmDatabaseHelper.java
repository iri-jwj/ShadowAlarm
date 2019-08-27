package com.android.deskclock.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.android.deskclock.model.ShadowAlarm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Deprecated
public class AlarmDatabaseHelper {
    private static final String TAG = "AlarmDatabaseHelper";

    public static final int ALARM_DATABASE_ADD_ALARM = 301;
    public static final int ALARM_DATABASE_DELETE_ALARM = 302;
    public static final int ALARM_DATABASE_UPDATE_ALARM = 303;
    public static final int ALARM_DATABASE_QUERY_ALARM = 304;

    private AlarmDatabase db;
    private SQLiteDatabase writableDb;


    public AlarmDatabaseHelper(Context context) {
        db = new AlarmDatabase(context);
    }

    public void insertNewAlarm(ShadowAlarm alarm) {
        writableDb = db.getWritableDatabase();
        writableDb.beginTransaction();
        ContentValues alarmValues = new ContentValues();
        alarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID, alarm.getId().toString());
        alarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL, alarm.getLabel());
        alarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR, alarm.getRemindHours());
        alarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE, alarm.getRemindMinutes());
        alarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK, alarm.getRemindDaysInWeek());
        writableDb.insert(AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, null, alarmValues);
        writableDb.setTransactionSuccessful();
        writableDb.close();
    }

    public void updateAlarm(ShadowAlarm newAlarm) {
        writableDb = db.getWritableDatabase();
        writableDb.beginTransaction();
        ContentValues newAlarmValues = new ContentValues();
        newAlarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL, newAlarm.getId().toString());
        newAlarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR, newAlarm.getRemindHours());
        newAlarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE, newAlarm.getRemindMinutes());
        newAlarmValues.put(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK, newAlarm.getRemindDaysInWeek());
        writableDb.update(AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, newAlarmValues, "id = ?",
                new String[]{newAlarm.getId().toString()});

        writableDb.setTransactionSuccessful();

        writableDb.close();
    }

   /* public List<ShadowAlarm> queryAlarm() {
        List<ShadowAlarm> alarmFromDb = new ArrayList<>();
        writableDb = db.getWritableDatabase();
        writableDb.beginTransaction();
        Cursor cursor = writableDb.query(AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, null, null, null,
                null, null, AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR + " ," + AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE + " ASC");
        writableDb.setTransactionSuccessful();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                alarmFromDb.add(new ShadowAlarm(
                        UUID.fromString(cursor.getString(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_ID))),
                        cursor.getString(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_LABEL)),
                        cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDHOUR)),
                        cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDMINUTE)),
                        cursor.getInt(cursor.getColumnIndex(AlarmDatabase.AlarmDatabaseEntity.COLUMN_REMINDDAYSINWEEK)))
                );

            }
        } else {
            Log.d(TAG, "it seems no alarm has added");
        }

        cursor.close();
        writableDb.close();
        return alarmFromDb;
    }*/

    public void deleteTargetAlarm(String id) {
        writableDb = db.getWritableDatabase();
        writableDb.beginTransaction();
        int result = writableDb.delete(AlarmDatabase.AlarmDatabaseEntity.TABLE_NAME, "id = ?", new String[]{id});
        writableDb.setTransactionSuccessful();
        if (result != 0) {
            Log.d(TAG, "delete alarm id =" + id + "success");
        }
        writableDb.close();
    }
}
