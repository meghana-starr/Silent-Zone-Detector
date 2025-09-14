package com.example.silentzonedetector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private EditText edtLatitude, edtLongitude, edtRadius, edtAddress;
    private Button btnSaveZone;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        edtLatitude = findViewById(R.id.edtLatitude);
        edtLongitude = findViewById(R.id.edtLongitude);
        edtRadius = findViewById(R.id.edtRadius);
        edtAddress = findViewById(R.id.edtAddress);
        btnSaveZone = findViewById(R.id.btnSaveZone);

        btnSaveZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latStr = edtLatitude.getText().toString();
                String lonStr = edtLongitude.getText().toString();
                String radiusStr = edtRadius.getText().toString();
                String address = edtAddress.getText().toString();  // <- added

                // Validate input
                if (latStr.isEmpty() || lonStr.isEmpty() || radiusStr.isEmpty() || address.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                float radius = Float.parseFloat(radiusStr);

                boolean inserted = dbHelper.addZone(lat, lon, radius, address); // <- include address

                if (inserted) {
                    Toast.makeText(AdminActivity.this, "Zone saved successfully", Toast.LENGTH_SHORT).show();
                    edtLatitude.setText("");
                    edtLongitude.setText("");
                    edtRadius.setText("");
                    edtAddress.setText("");
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to save zone", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
