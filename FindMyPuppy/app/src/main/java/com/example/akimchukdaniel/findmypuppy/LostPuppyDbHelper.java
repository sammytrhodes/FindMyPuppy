package com.example.akimchukdaniel.findmypuppy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.akimchukdaniel.findmypuppy.LostPuppy.LostPuppyEntry;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class LostPuppyDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LostPuppyEntry.TABLE_NAME + " (" +
                    LostPuppyEntry._ID + " INTEGER PRIMARY KEY," +
                    LostPuppyEntry.COLUMN_NAME_LOSTFOUND + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_NAME + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_SEX + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_BREED + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_FUR_COLOR + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_EYE + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_LAST_LOCATION + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_LAST_TIME + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_PHONE + " TEXT," +
                    LostPuppyEntry.COLUMN_NAME_REPORTER + " TEXT )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + LostPuppyEntry.TABLE_NAME;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LostPuppiesNEWwithlostfoundandreporters.db";


    public LostPuppyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDownGrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
