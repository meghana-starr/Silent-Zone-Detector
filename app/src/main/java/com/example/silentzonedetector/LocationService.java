package com.example.silentzonedetector;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class LocationService extends Service {

    private LocationManager locationManager;
    private AudioManager audioManager;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        dbHelper = new DatabaseHelper(this);

        startForegroundService();
        requestLocationUpdates();
    }

    private void startForegroundService() {
        String channelId = "silent_zone_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Silent Zone Detector",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Silent Zone Detector Running")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
                .build();

        startForeground(1, notification);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        checkZones(location);
                    }

                    @Override public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}
                    @Override public void onProviderEnabled(String provider) {}
                    @Override public void onProviderDisabled(String provider) {}
                });
    }

    private void checkZones(Location location) {
        List<Zone> zones = dbHelper.getAllZones();
        boolean inside = false;

        for (Zone zone : zones) {
            float[] distance = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    zone.getLatitude(), zone.getLongitude(), distance);
            if (distance[0] < zone.getRadius()) {
                inside = true;
                break;
            }
        }

        if (inside)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
