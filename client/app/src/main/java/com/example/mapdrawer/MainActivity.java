package com.example.mapdrawer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.MailTo;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView coordsTextView;
    Button updateButton;
    Button cancelButton;
    static ArrayList<Location> all;
    static Location current;
    Intent service;
    static RequestQueue queue;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (all == null) {
            all = new ArrayList<>();
        }
        coordsTextView = findViewById(R.id.coords);
        updateButton = findViewById(R.id.update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MainService.class));
                coordsTextView.setText("Сервер запущен");
            }
        });
        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), MainService.class));
//                MainService.IL.stop();
                MainService.locationManager.removeUpdates(MainService.locationListener);
                MainService.manager.cancel(1);
                coordsTextView.setText("Сервер выключен");
            }
        });
        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("Activity", "Destroy");
        super.onDestroy();
    }
}
