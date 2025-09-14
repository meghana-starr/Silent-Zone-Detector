package com.example.silentzonedetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnAdminPanel, btnStartDetector, btnStopDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        btnStartDetector = findViewById(R.id.btnStartDetector);
        btnStopDetector = findViewById(R.id.btnStopDetector);

        btnAdminPanel.setOnClickListener(v -> startActivity(new Intent(this, AdminPanelActivity.class)));

        btnStartDetector.setOnClickListener(v -> {
            if (checkPermissions()) {
                startService(new Intent(this, LocationService.class));
                Toast.makeText(this, "Detector started", Toast.LENGTH_SHORT).show();
            }
        });

        btnStopDetector.setOnClickListener(v -> {
            stopService(new Intent(this, LocationService.class));
            Toast.makeText(this, "Detector stopped", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                }, 100);
                return false;
            }
        }
        return true;
    }
}
