package com.jstechnologies.locationprovidermodule;

import android.content.Intent;
import android.widget.Toast;

public class LocationResponse {
    double latitude;
    double longitude;
    double altitude;
    float accuracy;
    float speed;
    long time;

    public LocationResponse() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public long getTime() {
        return time;
    }

    public LocationResponse(double latitude, double longitude, double altitude, float accuracy, float speed, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.time = time;
    }

    public static LocationResponse Parse(Intent intent)
    {
        double lat=intent.getDoubleExtra("com.jstechnologies.LOCATION_DATA_LAT",0.0);
        double lon=intent.getDoubleExtra("com.jstechnologies.LOCATION_DATA_LON",0.0);
        double alt=intent.getDoubleExtra("com.jstechnologies.LOCATION_DATA_ALT",0.0);
        float acc=intent.getFloatExtra("com.jstechnologies.LOCATION_DATA_ACC",0);
        float speed=intent.getFloatExtra("com.jstechnologies.LOCATION_DATA_SPEED",0);
        long time=intent.getLongExtra("com.jstechnologies.LOCATION_DATA_TIME",0);
        return new LocationResponse(lat,lon,alt,acc,speed,time);
    }
}
