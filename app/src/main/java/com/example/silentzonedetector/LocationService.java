package com.example.silentzonedetector;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
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
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Silent Zone Detector",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        startForeground(1, new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Silent Zone Detector Running")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
                .build());
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 10,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        checkZones(location);
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                    @Override public void onProviderEnabled(String provider) {}
                    @Override public void onProviderDisabled(String provider) {}
                });
    }

    private void checkZones(Location location) {
        List<Zone> zones = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int latIndex = cursor.getColumnIndex(DatabaseHelper.COL_LAT);
                int lonIndex = cursor.getColumnIndex(DatabaseHelper.COL_LON);
                int radIndex = cursor.getColumnIndex(DatabaseHelper.COL_RADIUS);

                if (latIndex != -1 && lonIndex != -1 && radIndex != -1) {
                    double lat = cursor.getDouble(latIndex);
                    double lon = cursor.getDouble(lonIndex);
                    float radius = cursor.getFloat(radIndex);
                    zones.add(new Zone(lat, lon, radius));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        boolean inside = false;
        for (Zone zone : zones) {
            float[] distance = new float[1];
            Location.distanceBetween(
                    location.getLatitude(),
                    location.getLongitude(),
                    zone.getLatitude(),
                    zone.getLongitude(),
                    distance
            );
            if (distance[0] <= zone.getRadius()) {
                inside = true;
                break;
            }
        }

        // Vice versa logic
        if (inside)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);   // Inside zone → Normal
        else
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  // Outside zone → Vibrate
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
