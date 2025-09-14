package com.example.silentzonedetector;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText etAddress, etRadius;
    private Button btnSaveZone;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        etAddress = findViewById(R.id.etAdd);
        etRadius = findViewById(R.id.etRad);
        btnSaveZone = findViewById(R.id.btnSaveZone);
        dbHelper = new DatabaseHelper(this);

        btnSaveZone.setOnClickListener(v -> {
            try {
                String addressStr = etAddress.getText().toString();
                String radiusStr = etRadius.getText().toString();

                if (addressStr.isEmpty() || radiusStr.isEmpty()) {
                    Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Address address = addresses.get(0);
                double lat = address.getLatitude();
                double lon = address.getLongitude();
                float radius = Float.parseFloat(radiusStr);

                boolean inserted = dbHelper.addZone(lat, lon, radius, addressStr);
                if (inserted) Toast.makeText(this, "Zone saved successfully", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Failed to save zone", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
