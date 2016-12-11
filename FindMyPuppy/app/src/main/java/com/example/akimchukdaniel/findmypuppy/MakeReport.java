package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import static android.content.ContentValues.TAG;

/**
 * Created by akimchukdaniel on 12/4/16.
 * Activity class for making reports of lost or found puppies.
 * Allows users to make a report of a lost or found puppy, entering information which will
 * be inputted into the database.
 */

public class MakeReport extends Activity {

    RadioGroup lostfound, sex;
    EditText name, breed, fur, eye;
    Place location;
    DatePicker date;
    Button submit;
    Cursor c;

    /**
     * Called when the Activity is created. Inflates the view, creates a pointer to the Google
     * Maps PlaceAutocompleteFragment and defines listeners for the fragment. Creates all other
     * pointers to views. Sets background color to the preferred color.
     * @param savedInstanceState
     */
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

        switch (preferences.getString("bgColor", "white")) { //set background to preferred color
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

    /**
     * Called when the submit button is clicked. Ensures all info is inputted (if not, prompts
     * the user to finish entering data). If it is, notifies the user if this report matches
     * an existing report. If it does not, submits this report to the database.
     * @param view
     */
    public void submit(View view) {
        //make sure all data is entered.
        if (lostfound.getCheckedRadioButtonId() == -1 ||
                name.getText().toString().equals("") ||
                breed.getText().toString().equals("") ||
                fur.getText().toString().equals("") ||
                eye.getText().toString().equals("") ||
                location == null ||
                sex.getCheckedRadioButtonId() == -1) {
            Toast error = Toast.makeText(getApplicationContext(), "Missing info",
                    Toast.LENGTH_SHORT);
            error.show();
            return;
        }

        //if the puppy is match, tell the user and give them the opportunity to go to the match's
        //report.
        if (isMatch()) {
            PuppyDialogFragment dialogFragment = new PuppyDialogFragment();
            dialogFragment.show(getFragmentManager(), "We found a puppy!!");
            return;
        }

        //Put all the info into a new row of the database.

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

        SharedPreferences sp = getSharedPreferences("preferences", MODE_PRIVATE);
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE, sp.getString("phone", ""));
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER, sp.getString("name", "Anonymous"));




        long newRowId = db.insert(LostPuppy.LostPuppyEntry.TABLE_NAME, null, values);
        //go back to the list
        finish();
    }


    /**
     * Checks the database to see if there is a report that matches the information inputted
     * in the current report.
     * A match has the same name, sex, and fur color, and opposite lost/found status
     * @return true if there is, false otherwise
     */
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
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER
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
        return c.moveToFirst(); //this returns true if there's at least one row, aka if there is
                                //matching report.
    }


}

