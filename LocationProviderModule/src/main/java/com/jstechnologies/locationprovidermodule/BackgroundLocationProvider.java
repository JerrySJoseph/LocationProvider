package com.jstechnologies.locationprovidermodule;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

public class BackgroundLocationProvider {

    public static String LOCATION_UPDATE_ACTION="com.jstechnologies.LOCATION_AVAILABLE";

    BackgroundLocationService service;
    int interval;
    Context context;
    Intent serviceIntent;
    public BackgroundLocationProvider(Context context,String notificationTitle,String notificationContent, int notificationIcon, int interval) {
        this.interval = interval;
        this.context=context;
        service=new BackgroundLocationService();
        serviceIntent=new Intent(context,service.getClass());
        serviceIntent.putExtra("nTitle",notificationTitle);
        serviceIntent.putExtra("nContent",notificationContent);
        serviceIntent.putExtra("nIcon",notificationIcon);
        serviceIntent.putExtra("interval",interval);
    }




    public BackgroundLocationProvider(Context context, int interval) {
        this.interval = interval;
        this.context=context;
        service=new BackgroundLocationService();
        serviceIntent=new Intent(context,service.getClass());
        serviceIntent.putExtra("interval",interval);

    }


    public void BeginUpdates()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                context.startService(serviceIntent);
            }
        }
        context.startService(serviceIntent);
    }
    public void StopUpdates()
    {
        context.stopService(serviceIntent);
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
}
