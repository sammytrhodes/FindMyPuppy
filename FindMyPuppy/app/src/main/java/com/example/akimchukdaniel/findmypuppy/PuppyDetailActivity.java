package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class PuppyDetailActivity extends Activity {

    TextView nameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String name = getName(id);

        nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(name);
    }

    public String getName(int id) {
        LostPuppyDbHelper dbHelper = new LostPuppyDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                LostPuppy.LostPuppyEntry._ID,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME,
        };

        String selection = LostPuppy.LostPuppyEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};

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
            return c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME));
        }
        return "No name?????";
    }
}
