package com.example.silentzonedetector;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AdminActivity extends AppCompatActivity {

    EditText etLatitude, etLongitude, etRadius;
    Button btnSave;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etRadius = findViewById(R.id.etRadius);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveZone();
            }
        });
    }

    private void saveZone() {
        String latStr = etLatitude.getText().toString().trim();
        String lonStr = etLongitude.getText().toString().trim();
        String radStr = etRadius.getText().toString().trim();

        if(latStr.isEmpty() || lonStr.isEmpty() || radStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latStr);
        double longitude = Double.parseDouble(lonStr);
        float radius = Float.parseFloat(radStr);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_LAT, latitude);
        values.put(DatabaseHelper.COL_LON, longitude);
        values.put(DatabaseHelper.COL_RADIUS, radius);

        long id = db.insert(DatabaseHelper.TABLE_NAME, null, values);
        if(id != -1) {
            Toast.makeText(this, "Zone saved successfully", Toast.LENGTH_SHORT).show();
            etLatitude.setText("");
            etLongitude.setText("");
            etRadius.setText("");
        } else {
            Toast.makeText(this, "Error saving zone", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
