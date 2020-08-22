package com.jstechnologies.locationprovider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.jstechnologies.locationprovidermodule.BackgroundLocationProvider;
import com.jstechnologies.locationprovidermodule.LocationReciever;
import com.jstechnologies.locationprovidermodule.LocationResponse;

public class LocationReciver extends LocationReciever {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BackgroundLocationProvider.LOCATION_UPDATE_ACTION.equals(intent.getAction())) {
            LocationResponse response=LocationResponse.Parse(intent);
            Toast.makeText(context,"Latitude: "+response.getLatitude()+", Longitude: "+response.getLongitude(),Toast.LENGTH_SHORT).show();
            Log.d("GPSPROVIDER",response.getLatitude()+","+response.getLongitude());
        }
    }
}
