package com.example.akimchukdaniel.findmypuppy;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LostPuppy> puppies;

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

        // Add a marker in Sydney and move the camera
        initializePuppyList();
        for (LostPuppy puppy : puppies) {
            LatLng location = puppy.getLocation();
            String lostfound = puppy.getLostfound();
            if (lostfound.equals("LOST")) {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(lostfound)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(lostfound)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

            }
        }
    }

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
