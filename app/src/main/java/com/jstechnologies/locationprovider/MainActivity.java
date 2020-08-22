package com.jstechnologies.locationprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jstechnologies.locationprovidermodule.BackgroundLocationProvider;
import com.jstechnologies.locationprovidermodule.ForegroundLocationProvider;

public class MainActivity extends AppCompatActivity {

    ForegroundLocationProvider provider;
    String TAG="GPSPROVIDER";
    BackgroundLocationProvider locationProviderbg;
    ForegroundLocationProvider locationProviderfg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        provider= new ForegroundLocationProvider(this, ForegroundLocationProvider.ProviderType.NETWORK);

        locationProviderbg= new BackgroundLocationProvider(this,2);

        //Registering Location update Reciever
        LocationReciver broadRec= new LocationReciver();
        IntentFilter filter = new IntentFilter(BackgroundLocationProvider.LOCATION_UPDATE_ACTION);
        registerReceiver(broadRec, filter);


    }
    LocationListener fgListener= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this,"Latitude: "+location.getLatitude()+", Longitude: "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            Log.d("GPSPROVIDER",location.getLatitude()+","+location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    public void onStartClick(View view) {
        locationProviderbg.BeginUpdates();
    }

    public void onStopClick(View view) {
        locationProviderbg.StopUpdates();
    }

    public void onStartClickForeground(View view) {
        provider.SubscribeForLocationUpdates(fgListener);
    }

    public void onStopClickForeground(View view) {
        provider.dismissProvider();
    }
}