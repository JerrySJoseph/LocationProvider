package com.jstechnologies.locationprovidermodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class LocationReciever extends BroadcastReceiver {
    @Override
    public abstract void onReceive(Context context, Intent intent) ;
}
