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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by akimchukdaniel on 12/1/16.
 * Displays all relevant information about a puppy, also places a marker on a map fragment to show
 * the puppy's location.
 */

public class PuppyDetailActivity extends Activity implements OnMapReadyCallback {

    TextView nameTextView;
    MapFragment mapFragment;
    LatLng location;
    String name;

    /**
     * Called when activity is created. Sets up pointers, and populates the TextView with all the
     * information provided in the intent. Initializes map.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puppy_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        name = intent.getStringExtra("name");
        String[] locationStr = intent.getStringExtra("location").split(",");
        //get a LatLng object from the comma separated string "LAT,LNG"
        location = new LatLng(Double.parseDouble(locationStr[0]), Double.parseDouble(locationStr[1]));

        String date = intent.getStringExtra("date");
        mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapfragment, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        nameTextView = (TextView) findViewById(R.id.name);
        String theText = intent.getStringExtra("lostfound") + "\n" + name +  "\nBreed: " + intent.getStringExtra("breed") + "\nFur Color: "
                + intent.getStringExtra("fur") + "\nDate Last Seen: " + date + "\nSex: " + intent.getStringExtra("sex")
                + "\nEye Color: " + intent.getStringExtra("eye") + "\nREPORTED BY:\n" + intent.getStringExtra("reporter") + "\n";
        if (!intent.getStringExtra("phone").equals("")) {
            theText += intent.getStringExtra("phone");
        } else {
            theText += "No contact info provided";
        }
        nameTextView.setText(theText);
}

    /**
     * Called when the map is ready. Places a marker for the puppy. Azure if it is lost,
     * magenta if found.
     * @param map
     */
    public void onMapReady(GoogleMap map) {
        if (getIntent().getStringExtra("lostfound").equals("LOST")) {
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        } else {
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }
        //center on the location.
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(location, 8);
        map.moveCamera(cu);
    }
}
