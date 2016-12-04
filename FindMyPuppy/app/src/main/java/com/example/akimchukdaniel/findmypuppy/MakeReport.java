package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
    }

    public void submit(View view) {
        LostPuppyDbHelper dbHelper = new LostPuppyDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED, breed.getText().toString());
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE, eye.getText().toString());
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR, fur.getText().toString());


        try {
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION, getBytes(location.getLatLng()));
            values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME, getBytes(new Date(date.getYear(), date.getMonth(), date.getDayOfMonth())));
        } catch (Exception e) {

        }
        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME, name.getText().toString());

        values.put(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX, ((RadioButton) findViewById(sex.getCheckedRadioButtonId())).getContentDescription().toString());

        long newRowId = db.insert(LostPuppy.LostPuppyEntry.TABLE_NAME, null, values);
        finish();
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

