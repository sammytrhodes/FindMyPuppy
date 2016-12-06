package com.example.akimchukdaniel.findmypuppy;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class PuppyDetailActivity extends Activity implements OnMapReadyCallback {

    TextView nameTextView;
    MapFragment mapFragment;
    LatLng location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String name = intent.getStringExtra("name");
        String[] locationStr = intent.getStringExtra("location").split(",");

        location = new LatLng(Double.parseDouble(locationStr[0]), Double.parseDouble(locationStr[1]));

        String date = intent.getStringExtra("date");
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapfragment, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(name + ", " + location + ", " + date);
    }

    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(location)
                .title("Marker"));

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(location, 8);
        map.moveCamera(cu);
    }
}
