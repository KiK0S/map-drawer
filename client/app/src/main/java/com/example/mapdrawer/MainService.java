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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TimeUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class MainService extends Service {
    private static LocationManager locationManager;
    static String API_URL = "http://104.140.100.118:5000/add";
    private static LocationListener locationListener;
    private static NotificationManager manager;

    private static int sendData(final Location location) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Service", "got response ");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Service", error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("x", Double.toString(location.getLatitude()));
                params.put("y", Double.toString(location.getLongitude()));
                return params;
            }
        };
        MainActivity.queue.add(stringRequest);
        return 0;
    }

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
                sendData(location);
                MainActivity.all.add(location);
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Service", "onStatushanged");
                sendData(locationManager.getLastKnownLocation(provider));
                MainActivity.all.add(locationManager.getLastKnownLocation(provider));
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Service", "onProviderEnabled");
                sendData(locationManager.getLastKnownLocation(provider));
                MainActivity.all.add(locationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 20, 50, locationListener);
        Log.d("Service", "started in foreground");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "random name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Mapdrawer_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "DrawMap Channel",
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        manager.notify(1, builder.setContentText("Я слежу за тобой").setSmallIcon(R.drawable.nature).setContentTitle("Соединен с сервером").build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "destroy");
        locationManager.removeUpdates(locationListener);
        manager.cancel(1);
        super.onDestroy();
    }
}

