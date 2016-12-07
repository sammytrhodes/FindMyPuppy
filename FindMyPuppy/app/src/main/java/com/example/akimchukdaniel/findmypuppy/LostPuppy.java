package com.example.akimchukdaniel.findmypuppy;

import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by akimchukdaniel on 12/1/16.
 */

public class LostPuppy {

    private String name;
    private String breed;
    private String fur;
    private LatLng location;
    private java.util.Date date;
    private String sex;
    private String eye;
    private int id;
    private String lostfound;

    private LostPuppy() {

    }

    public LostPuppy(int id, String name, String breed, String fur, LatLng location, java.util.Date date, String sex, String eye, String lostfound) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.fur = fur;
        this.location = location;
        this.date = date;
        this.sex = sex;
        this.eye = eye;
        this.lostfound = lostfound;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String getBreed() {return breed;}
    public String getFur() {return fur;}
    public LatLng getLocation() {return location;}
    public java.util.Date getDate() {return date;}
    public String getSex() {return sex;}
    public String getEye() {return eye;}
    public String getLostfound() {return lostfound;}

    public static class LostPuppyEntry implements BaseColumns {
        public static final String TABLE_NAME = "lost_puppies";
        public static final String COLUMN_NAME_NAME= "name";
        public static final String COLUMN_NAME_BREED= "breed";
        public static final String COLUMN_NAME_FUR_COLOR= "fur_color";
        public static final String COLUMN_NAME_LAST_LOCATION= "last_seen_location";
        public static final String COLUMN_NAME_LAST_TIME= "last_seen_time";
        public static final String COLUMN_NAME_SEX= "sex";
        public static final String COLUMN_NAME_EYE= "eye_color";
        public static final String COLUMN_NAME_LOSTFOUND= "lostfound";
    }

    public String toString() {
        return lostfound + ": " + name + ", " + breed;
    }
}
