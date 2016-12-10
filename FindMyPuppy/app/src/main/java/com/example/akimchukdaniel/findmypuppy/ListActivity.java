package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 * Created by akimchukdaniel on 12/1/16.
 * The Activity class that will display all lost and found puppies in a list with the ability
 * to filter via search. Access to all other Activities will be here as well.
 */

public class ListActivity extends Activity {

    ListView listView;
    List<LostPuppy> puppyList;
    static boolean cameFromMap = false; //this is to make the default view preference behave correctly
    Menu menu;

    /**
     * Called when the Activity is created. Inflates the view, gets a pointer to the ListView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_list);
        listView = (ListView) findViewById(R.id.listView);
    }

    /**
     * Called after onCreate upon start, and also whenever a child activity ends and we return to
     * this activity. Calls the function that will initialize the list from the db and populate the
     * ListView. Also accesses preferences to go to the chosen default view and background color.
     */
    @Override
    protected void onResume() {
        super.onResume();
        initializePuppyList(new String[]{""});

        SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);

        if (preferences.getString("defaultView", "list").equals("map") && !cameFromMap) {
            //if the preference is to default to the map and the user didn't just come from the map,
            //go to the map.
            Intent intent = new Intent(this, MapsActivity.class);
            cameFromMap = true;
            startActivity(intent);
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.listLayout);

        switch (preferences.getString("bgColor", "white")) { //set the background color to the chosen color
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
     * Populates the ArrayList puppyList and the ListView with information from the database, ensuring
     * that any words in the query exist in the toString of the puppy.
     * @param query a list of words/phrases that must be present in the puppies toString. If they
     *              aren't there, do not show.
     */
    public void initializePuppyList(String[] query) {
        puppyList = new ArrayList<>();
        final ArrayAdapter<LostPuppy> mPuppyAdapter = //set up the adapter, connecting to the proper views
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
                LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE,
                LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER
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
            String lostfound = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND));
            String phone = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE));
            String reporter = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER));

            LatLng location = new LatLng(0, 0);
            if (locStr != null) {
                location = new LatLng(Double.parseDouble(locStr.split(",")[0]), Double.parseDouble(locStr.split(",")[1]));
            }

            Date date = new Date(0);
            if (dateStr != null) {
                String[] dateArray = dateStr.split(",");
                date = new Date(Integer.parseInt(dateArray[2]) - 1900, Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
            }

            LostPuppy thePuppy = new LostPuppy(id, name, breed, fur, location, date, sex, eye, lostfound, phone, reporter);

            boolean notInList = false;
            for (String s : query) { //if any part of the query isn't in the toString, the puppy shouldn't be in the list
                if (!thePuppy.toString().toLowerCase().contains(s.toLowerCase())){
                    notInList = true;
                    break;
                }
            }
            if (!notInList) { //only add the puppy if they fit the query
                puppyList.add(thePuppy);
            }
            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(LostPuppy.LostPuppyEntry._ID));
                name = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME));
                breed = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED));
                fur = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR));
                dateStr = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME));
                locStr = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION));
                phone = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE));
                reporter = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER));

                location = new LatLng(0, 0);
                if (locStr != null) {
                    location = new LatLng(Double.parseDouble(locStr.split(",")[0]), Double.parseDouble(locStr.split(",")[1]));
                }

                date = new Date(0);
                if (dateStr != null) {
                    String[] dateArray = dateStr.split(",");
                    date = new Date(Integer.parseInt(dateArray[2]) - 1900, Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
                }


                sex = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX));
                eye = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE));
                lostfound = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND));
                thePuppy = new LostPuppy(id, name, breed, fur, location, date, sex, eye, lostfound, phone, reporter);
                notInList = false;
                for (String s : query) { //if any part of the query isn't in the toString, the puppy shouldn't be in the list
                    if (!thePuppy.toString().toLowerCase().contains(s.toLowerCase())){
                        notInList = true;
                        break;
                    }
                }
                if (!notInList) { //only add the puppy if they fit the query
                    puppyList.add(thePuppy);
                }

            }
        }
        listView.setAdapter(mPuppyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if a list item is clicked, pack all the info about the puppy as extras in the
                //intent and go to the detail view
                cameFromMap = false;
                Intent intent = new Intent(getApplicationContext(), PuppyDetailActivity.class);
                intent.putExtra("id", mPuppyAdapter.getItem(i).getId());
                intent.putExtra("name", mPuppyAdapter.getItem(i).getName());
                intent.putExtra("breed", mPuppyAdapter.getItem(i).getBreed());
                intent.putExtra("fur", mPuppyAdapter.getItem(i).getFur());
                intent.putExtra("sex", mPuppyAdapter.getItem(i).getSex());
                intent.putExtra("eye", mPuppyAdapter.getItem(i).getEye());
                intent.putExtra("location", mPuppyAdapter.getItem(i).getLocation().latitude + "," + mPuppyAdapter.getItem(i).getLocation().longitude);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                intent.putExtra("date", sdf.format(mPuppyAdapter.getItem(i).getDate()));
                intent.putExtra("lostfound", mPuppyAdapter.getItem(i).getLostfound());
                intent.putExtra("phone", mPuppyAdapter.getItem(i).getPhone());
                intent.putExtra("reporter", mPuppyAdapter.getItem(i).getReporter());
                startActivity(intent);

            }

        });
    }

    /**
     * This function is called when the ActionBar is created. Creates the pointer to the menu,
     * Sets up the listeners for the search bar
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        this.menu = menu;
        final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String query) {
                        //splits whatever's entered in the search bar on spaces, and then repopulates
                        //the list with only entries that match
                        initializePuppyList(query.split(" "));
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //hide the keyboard if the user "submits" the search (hits the done button)
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                        return true;
                    }

                });
        return true;
    }

    /**
     * Called whenever any button in the ActionBar is selected. Directs to the proper action
     * depending on which item was pressed.
     * @param item a pointer to the item that was selected
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                //go to the make report activity
                cameFromMap = false;
                Intent intent = new Intent(this, MakeReport.class);
                startActivity(intent);
                return true;

            case R.id.mapButton:
                //go to the map
                cameFromMap = true;
                Intent mapsIntent = new Intent(this, MapsActivity.class);
                startActivity(mapsIntent);
                return true;

            case R.id.settings:
                //go to settings
                cameFromMap = false;
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
