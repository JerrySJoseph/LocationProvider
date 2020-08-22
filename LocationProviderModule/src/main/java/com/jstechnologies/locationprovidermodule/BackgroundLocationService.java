package com.jstechnologies.locationprovidermodule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.TimeUnit;


public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "GPSPROVIDER";
    private final String TAG_LOCATION = "GPSPROVIDER";
    private Context context;
    private boolean stopService = false;
    /* For Google Fused API */
    protected GoogleApiClient mGoogleApiClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private String latitude = "0.0", longitude = "0.0";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    int interval;
    private Location mCurrentLocation;
    /* For Google Fused API */
    String notificationTitle, notificationContent;
    int notificationIcon;
    public BackgroundLocationService() {

    }



    void PopVars(Intent myIntent)
    {
        if (myIntent !=null && myIntent.getExtras()!=null) {
            notificationTitle = myIntent.getStringExtra("nTitle");
            notificationContent = myIntent.getStringExtra("nContent");
            interval=myIntent.getIntExtra("interval",10);
            notificationIcon=myIntent.getIntExtra("nIcon",R.drawable.ic_location);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PopVars(intent);
        StartForeground();
        Log.d(TAG, "Service Startcomand");
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!stopService) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (!stopService) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(interval));
                    }
                }
            }
        };
        handler.postDelayed(runnable, 2000);

        buildGoogleApiClient();

        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        context = this;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void StartForeground() {


        String CHANNEL_ID = "gps_channel";
            String CHANNEL_NAME = "gps_channel";

            NotificationCompat.Builder builder = null;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                builder.setChannelId(CHANNEL_ID);
                builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
            } else {
                builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            }
            if(notificationTitle!=null && !notificationTitle.isEmpty())
                builder.setContentTitle(notificationTitle);
            else
                builder.setContentTitle("GPS Provider");
            builder.setContentText(notificationContent!=null?notificationContent:"You are now online");
            Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(notificationSound);
            builder.setSmallIcon(notificationIcon);
            Notification notification = builder.build();

        startForeground(101, notification);
    }

    protected synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG_LOCATION, "Location Received");
                mCurrentLocation = locationResult.getLastLocation();
                sendBroadcast(locationResult);
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG_LOCATION, "Location Availability");
                super.onLocationAvailability(locationAvailability);
            }
        };
    }
    public void sendBroadcast(LocationResult locationResult) {
        Intent intent = new Intent(BackgroundLocationProvider.LOCATION_UPDATE_ACTION);
        intent.putExtra("com.jstechnologies.LOCATION_DATA_LAT", locationResult.getLastLocation().getLatitude());
        intent.putExtra("com.jstechnologies.LOCATION_DATA_LON", locationResult.getLastLocation().getLongitude());
        intent.putExtra("com.jstechnologies.LOCATION_DATA_ALT", locationResult.getLastLocation().getAltitude());
        intent.putExtra("com.jstechnologies.LOCATION_DATA_ACC", locationResult.getLastLocation().getAccuracy());
        intent.putExtra("com.jstechnologies.LOCATION_DATA_SPEED", locationResult.getLastLocation().getSpeed());
        intent.putExtra("com.jstechnologies.LOCATION_DATA_TIME", locationResult.getLastLocation().getTime());
        sendBroadcast(intent);
    }
    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service Stopped");
        stopService = true;
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.d(TAG_LOCATION, "Location Update Callback Removed");
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.d(TAG_LOCATION, "GPS Success");
                        requestLocationUpdate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            int REQUEST_CHECK_SETTINGS = 214;
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.d(TAG_LOCATION, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG_LOCATION, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d(TAG_LOCATION, "checkLocationSettings -> onCanceled");
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectGoogleClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }
    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
}
