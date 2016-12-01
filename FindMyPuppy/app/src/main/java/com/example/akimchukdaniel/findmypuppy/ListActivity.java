package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class ListActivity extends Activity {

    ListView listView;
    List<String> puppyList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_list);
        puppyList = new ArrayList<>();
        initData();
        initializePuppyList();

        final ArrayAdapter<String> mPuppyAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.puppy_list_item,
                        R.id.list_item_puppy_textview,
                        puppyList
                );

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mPuppyAdapter);

    }

    public void initializePuppyList() {
        LostPuppyDbHelper dbHelper = new LostPuppyDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                LostPuppy.LostPuppyEntry._ID,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE
        };

        String selection = LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME + " != ?";
        String[] selectionArgs = {""};

        String sortOrder = LostPuppy.LostPuppyEntry._ID + " ASC";

        Cursor c = db.query(
                LostPuppy.LostPuppyEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (c.moveToFirst()) {
            puppyList.add(c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME)));
            while (c.moveToNext()) {
                puppyList.add(c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME)));
            }
        }
    }

    public void initData() {
        LostPuppyDbHelper dbHelper = new LostPuppyDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Date sampleDate = new Date(Date.UTC(2016, 11, 10, 9, 10, 00));
        LatLng sampleLocation = new LatLng(-34, 151);



        ContentValues values = new ContentValues();
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED, "beagle");
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE, "blue");
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR, "brown");
        try {
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION, getBytes(sampleLocation));
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME, getBytes(sampleDate));
        } catch (IOException e) {

        }
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME, "testing!");
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX, "male");

        long newRowId = db.insert(LostPuppy.LostPuppyEntry.TABLE_NAME, null, values);
    }

    public static byte[] getBytes(Object obj) throws java.io.IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte [] data = bos.toByteArray();
        return data;
    }
}
