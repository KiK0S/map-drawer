package com.example.mapdrawer;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TimeUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MainService extends Service {
    static LocationManager locationManager;
    static LocationListener locationListener;
    static NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Service", "onLocationChanged");
                MainActivity.all.add(location);
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Service", "onStatushanged");
                MainActivity.all.add(locationManager.getLastKnownLocation(provider));
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Service", "onProviderEnabled");
                MainActivity.all.add(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, -1, locationListener);
        Log.d("Service", "started in foreground");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, new Notification.Builder(this, "drawmap").setContentText("content text").setSmallIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nature))).setContentTitle("content title").build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "destroy");
        locationManager.removeUpdates(locationListener);
        manager.cancel(1);
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("Service", "stop");
        locationManager.removeUpdates(locationListener);
        manager.cancel(1);
        return super.stopService(name);
    }

}

