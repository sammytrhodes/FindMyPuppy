package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by akimchukdaniel on 12/4/16.
 */

public class MakeReport extends Activity {

    RadioGroup lostfound, sex;
    EditText name, breed, fur, eye;
    Place location;
    DatePicker date;
    Button submit;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_report);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                location = place;
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        lostfound = (RadioGroup) findViewById(R.id.lostfound);
        sex = (RadioGroup) findViewById(R.id.sex);
        name = (EditText) findViewById(R.id.report_name);
        breed = (EditText) findViewById(R.id.report_breed);
        fur = (EditText) findViewById(R.id.report_fur);
        eye = (EditText) findViewById(R.id.report_eye);
        date = (DatePicker) findViewById(R.id.datePicker2);
        submit = (Button) findViewById(R.id.submit);
        LinearLayout layout = (LinearLayout) findViewById(R.id.reportLayout);
        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        switch (preferences.getString("bgColor", "white")) {
            case "white":
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "magenta":
                layout.setBackgroundColor(getResources().getColor(R.color.magenta));
                break;
            case "blue":
                layout.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "orange":
                layout.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case "green":
                layout.setBackgroundColor(getResources().getColor(R.color.green));
                break;
        }

    }

    public void submit(View view) {
        if (isMatch()) {
            PuppyDialogFragment dialogFragment = new PuppyDialogFragment();
            dialogFragment.show(getFragmentManager(), "We found a puppy!!");
            return;
        }



        LostPuppyDbHelper dbHelper = new LostPuppyDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED, breed.getText().toString());
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE, eye.getText().toString());
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR, fur.getText().toString());

        String locStr = location.getLatLng().latitude + "," + location.getLatLng().longitude;
        String dateString = date.getMonth() + "," + date.getDayOfMonth() + "," + date.getYear();
        try {
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION, locStr);
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME, dateString);
        } catch (Exception e) {

        }
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME, name.getText().toString());

        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX, ((RadioButton) findViewById(sex.getCheckedRadioButtonId())).getContentDescription().toString());

        switch (lostfound.getCheckedRadioButtonId()) {
            case R.id.lostbutton:
                values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND, "LOST");
                break;
            case R.id.foundbutton:
                values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND, "FOUND");
                break;
        }


        long newRowId = db.insert(LostPuppy.LostPuppyEntry.TABLE_NAME, null, values);
        finish();
    }


    public boolean isMatch() {
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
                LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND
        };

        String selection = LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME + " == ? AND " +
                LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX + " == ? AND " +
                LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR + " == ? AND " +
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND + " != ?";
        String[] selectionArgs = {name.getText().toString(),
                findViewById(sex.getCheckedRadioButtonId()).getContentDescription().toString(),
                fur.getText().toString(),
                findViewById(lostfound.getCheckedRadioButtonId()).getContentDescription().toString()
        };

        String sortOrder = LostPuppy.LostPuppyEntry._ID + " ASC";

        c = db.query(
                LostPuppy.LostPuppyEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return c.moveToFirst();
    }


}

