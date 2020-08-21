package com.jstechnologies.locationprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.jstechnologies.locationprovidermodule.LocationProvider;

public class MainActivity extends AppCompatActivity {

    LocationProvider provider;
    String TAG="GPSPROVIDER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        provider= new LocationProvider(this, LocationProvider.ProviderType.NETWORK);
        provider.setTimeinterval(1);
        provider.SubscribeForLocationUpdates(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG,"Lat:"+location.getLatitude()+"Long:"+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.e(TAG,bundle.toString());
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.e(TAG,"Provider Enabled :"+s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.e(TAG,"Provider Disabled :"+s);
            }
        });

    }
}