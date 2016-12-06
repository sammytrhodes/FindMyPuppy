package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    List<LostPuppy> puppyList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_list);
        //initData();
        listView = (ListView) findViewById(R.id.listView);
        initializePuppyList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePuppyList();
    }

    public void initializePuppyList() {
        puppyList = new ArrayList<>();
        final ArrayAdapter<LostPuppy> mPuppyAdapter =
                new ArrayAdapter<LostPuppy>(
                        this,
                        R.layout.puppy_list_item,
                        R.id.list_item_puppy_textview,
                        puppyList
                );


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
            int id = c.getInt(c.getColumnIndex(LostPuppy.LostPuppyEntry._ID));
            String name = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME));
            String breed = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED));
            String fur = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR));
            String locStr = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION));
            String dateStr = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME));
            String sex = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX));
            String eye = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE));

            LatLng location = new LatLng(0, 0);
            if (locStr != null) {
                location = new LatLng(Double.parseDouble(locStr.split(",")[0]), Double.parseDouble(locStr.split(",")[1]));
            }
            Date date = new Date(0);
            if (dateStr != null) {
                String[] dateArray = dateStr.split(",");
                date = new Date(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
            }

            puppyList.add(new LostPuppy(id, name, breed, fur, location, date, sex, eye));
            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(LostPuppy.LostPuppyEntry._ID));
                name = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME));
                breed = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED));
                fur = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR));

                location = new LatLng(0, 0);
                if (locStr != null) {
                    location = new LatLng(Double.parseDouble(locStr.split(",")[0]), Double.parseDouble(locStr.split(",")[1]));
                }
                date = new Date(0);
                if (dateStr != null) {
                    String[] dateArray = dateStr.split(",");
                    date = new Date(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
                }


                sex = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX));
                eye = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE));
                puppyList.add(new LostPuppy(id, name, breed, fur, location, date, sex, eye));

            }
        }
        listView.setAdapter(mPuppyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PuppyDetailActivity.class);
                intent.putExtra("id", mPuppyAdapter.getItem(i).getId());
                intent.putExtra("name", mPuppyAdapter.getItem(i).getName());
                intent.putExtra("breed", mPuppyAdapter.getItem(i).getBreed());
                intent.putExtra("fur", mPuppyAdapter.getItem(i).getFur());
                intent.putExtra("sex", mPuppyAdapter.getItem(i).getSex());
                intent.putExtra("eye", mPuppyAdapter.getItem(i).getEye());
                intent.putExtra("location", mPuppyAdapter.getItem(i).getLocation().latitude + "," + mPuppyAdapter.getItem(i).getLocation().longitude);
                intent.putExtra("date", mPuppyAdapter.getItem(i).getDate().toString());
                startActivity(intent);

            }

        });
    }
/*
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
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(this, MakeReport.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
