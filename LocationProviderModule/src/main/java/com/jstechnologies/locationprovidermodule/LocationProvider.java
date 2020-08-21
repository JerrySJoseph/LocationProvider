package com.jstechnologies.locationprovidermodule;

/**
     Title: A simple Android Library written in JAVA by Jerin Sebastian

     Description: This library is used to simplify the process of fetching
     Location in a Location enabled device via System Services.
     This library also supports background location fetching at regular
     intervals which might be resource intensive( may consume data and drain
     battery easily.
     Also, the developer do not intend to interfere in privacy of any individual
     nor this library shall be used for the same. Every individuals location information
     shall be recieved, sent or modified which his/her consent only....
 */
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class LocationProvider {


    public enum ProviderType{
        GPS,
        NETWORK
    }
    Context context;
    private ProviderType _providerType;
    private LocationManager locManager;
    private LocationListener locationListener;
    private long timeinterval=5;
    private long distanceinterval=0;
    /**
    * @param context is required for getting the System Service
    * @param providerType is an enum defined in this class for determining which type of location service is required
     */
    public LocationProvider(Context context,ProviderType providerType) {
        this._providerType = providerType;
        this.context=context;
        locManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


    }

    public void setTimeinterval(long timeinterval) {
        this.timeinterval = timeinterval;
    }

    public void setDistanceinterval(long distanceinterval) {
        this.distanceinterval = distanceinterval;
    }

    public void SubscribeForLocationUpdates(LocationListener locationListener)
    {
        this.locationListener=locationListener;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                if(_providerType.equals(ProviderType.GPS))
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeinterval, distanceinterval, locationListener);
                else
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeinterval, distanceinterval, locationListener);
            }
        }
    }
    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(((Activity)context), Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.GPS_Alert_Message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.GPS_Alert_Message).setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName())));
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }
    public void dismissProvider()
    {
        if(locManager!=null && locationListener!=null)
            locManager.removeUpdates(locationListener);
    }

}
