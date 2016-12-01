package com.example.akimchukdaniel.findmypuppy;

import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class LostPuppy {

    private LostPuppy() {

    }

    public static class LostPuppyEntry implements BaseColumns {
        public static final String TABLE_NAME = "lost_puppies";
        public static final String COLUMN_NAME_NAME= "name";
        public static final String COLUMN_NAME_BREED= "breed";
        public static final String COLUMN_NAME_FUR_COLOR= "fur_color";
        public static final String COLUMN_NAME_LAST_LOCATION= "last_seen_location";
        public static final String COLUMN_NAME_LAST_TIME= "last_seen_time";
        public static final String COLUMN_NAME_SEX= "sex";
        public static final String COLUMN_NAME_EYE= "eye_color";
    }
}
