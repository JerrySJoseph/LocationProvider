# LocationProvider
A simple GPS and Network Location Library for fetching location foreground and background. Its uses google's Fused Location provider API, 
therefore users might need to update google-play-services to the latest version available.

## About 
This library is used to simplify the process of fetching
     Location in a GPS enabled device via System Services.
     This library also supports background location fetching at regular
     intervals which might be resource intensive( may consume data and drain
     battery easily.
     Also, the developer do not intend to interfere in privacy of any individual
     nor this library shall be used for the same. Every individuals location information
     shall be recieved, sent or modified which his/her consent only....

## How to Include in your project

Include in build.gradle (project level)

```
allprojects {
    repositories {
        ....
        maven { url 'https://jitpack.io' }
    }
}
```

Include in build.gradle (app level)
```
dependencies {
    ...
    //Location Provider Library
    implementation 'com.github.JerrySJoseph:LocationProvider:1.0.0'  //Change the version to the current latest release
}
```

## Usage

### Permissions
Be sure to add these in your manifest

```xml
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

### Foreground location

Create an instance of ForegroundLocationProvider in Activity or fragment

```java
ForegroundLocationProvider locationProviderfg = new ForegroundLocationProvider(this, ForegroundLocationProvider.ProviderType.NETWORK);

```
Then, Subscribe for location updates....

```java
provider.SubscribeForLocationUpdates(fgListener);

```
Also, define a Location listener for recieving Location Updates....

```java
LocationListener fgListener= new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this,"Latitude: "+location.getLatitude()+", Longitude: "+location.getLongitude(),Toast.LENGTH_SHORT).show();
            Log.d("GPSPROVIDER",location.getLatitude()+","+location.getLongitude());
        }

         @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //Called when status of provider is changed
        }

        @Override
        public void onProviderEnabled(String s) {
            //Called when provider is enabled
        }

        @Override
        public void onProviderDisabled(String s) {
            //Called when provied is disabled
        }
    };

```

### Background Location
After recent updates of Android, we cannot fetch location in background without notifying the user of the same. So, This library provides a method to either 
Show a custom notification or a default to the user when it fetches location at regular intervals.
Also, to recieve location updates uninterrupted, this library provides a provision to recieve a custom broadcast (which can also be recived in other apps too..) sent
by the library. These are the steps to fire it up....

Create an instance of BackgroundLocationProvider in Activity or fragment
```java
        //Initiating background location Service
        // pass the context and interval to the provider constructor
        BackgroundLocationProvider provider= new BackgroundLocationProvider(this,5);
        
```
Create a new Reciever extending LocationReciever class in the library
```java
  class Reciever extends LocationReciever{

        @Override
        public void onReceive(Context context, Intent intent) {
            //Parsing the intent response to location data using LocationResponse.Parse() method
            LocationResponse response= LocationResponse.Parse(intent);
            Toast.makeText(MainActivity.this,"Lat:"+response.getLatitude(),Toast.LENGTH_SHORT).show();
        }
    }
```
Register the reciver to recieve updates using this code inside Activity or fragment
```java
        //Registering reciever
        IntentFilter filter = new IntentFilter(BackgroundLocationProvider.LOCATION_UPDATE_ACTION);
        registerReceiver(reciever, filter);
```
Lets listen to updates now.......
```java
        //Begin updates
        provider.BeginUpdates();
```
You can now listen to GPS location updates at set intervals even if we close the application.....

Updates
-----------
* Display custom notification.
* Location updates can be recieved in other apps to by registering to 'BackgroundLocationProvider.LOCATION_UPDATE_ACTION'.



License
-------

    Copyright 2014 - 2021 Jerin Sebastian

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
