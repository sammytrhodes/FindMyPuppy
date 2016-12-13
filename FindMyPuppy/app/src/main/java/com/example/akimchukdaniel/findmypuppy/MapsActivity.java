package com.example.akimchukdaniel.findmypuppy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimchukdaniel on 12/4/16.
 * Activity class for the map. Displays markers for all puppy reports.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private List<LostPuppy> puppies;

    /**
     * Called when the activity is created. Initializes the map fragment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * Also places markers on the map for each puppy.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] permissions = new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        //Add a marker for each puppy.
        initializePuppyList();
        for (LostPuppy puppy : puppies) {
            LatLng location = puppy.getLocation();
            String lostfound = puppy.getLostfound();
            String name = puppy.getName();
            String breed= puppy.getBreed();
            String color = puppy.getFur();
            String gender = puppy.getSex();
            if (lostfound.equals("LOST")) { //azure marker for lost puppies
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(lostfound)
                        .snippet(name + ", "  + color + " " + breed + ", " + gender )
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else { //magenta marker for found puppies
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(lostfound)
                        .snippet(color + " " + breed + ", " + gender)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            }
        }
        mMap.setOnMarkerClickListener(this);
    }
    public boolean onMarkerClick(Marker marker) {

        String location = marker.getPosition().latitude + "," +  marker.getPosition().longitude;
        LostPuppyDbHelper puppers = new LostPuppyDbHelper(this);
        SQLiteDatabase db = puppers.getReadableDatabase();
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
        String selection = LostPuppy. LostPuppyEntry.COLUMN_NAME_LAST_LOCATION + "= ?";
        String[] selectionArgs = {location};

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
        c.moveToFirst();
        Intent clickInt = new Intent(this, PuppyDetailActivity.class);
        clickInt.putExtra("id", c.getInt(c.getColumnIndex(LostPuppy.LostPuppyEntry._ID)));
        clickInt.putExtra("name", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME)));
        clickInt.putExtra("breed", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED)));
        clickInt.putExtra("fur", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR)));
        clickInt.putExtra("sex", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX)));
        clickInt.putExtra("eye", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE)));
        clickInt.putExtra("location", location);
        clickInt.putExtra("date", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME)).replace(",", "/"));
        clickInt.putExtra("lostfound", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND)));
        clickInt.putExtra("phone", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE)));
        clickInt.putExtra("reporter", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER)));
        startActivity(clickInt);
//        // Retrieve the data from the marker.
//        Integer clickCount = (Integer) marker.getTag();
//
//        // Check if a click count was set, then display the click count.
//        if (clickCount != null) {
//            clickCount = clickCount + 1;
//            marker.setTag(clickCount);
//            Toast.makeText(this,
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        // Return false to indicate that we have not consumed the event and that we wish
//        // for the default behavior to occur (which is for the camera to move such that the
//        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }
    /**
     * Sets up the list of puppies from the database.
     */
    public void initializePuppyList() {
        puppies = new ArrayList<>();

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
                date = new Date(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
            }

            puppies.add(new LostPuppy(id, name, breed, fur, location, date, sex, eye, lostfound, phone, reporter));
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
                    date = new Date(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]));
                }


                sex = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX));
                eye = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE));
                lostfound = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND));
                puppies.add(new LostPuppy(id, name, breed, fur, location, date, sex, eye, lostfound, phone, reporter));

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        onMapReady(mMap);
    }
}
