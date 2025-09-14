package com.example.silentzonedetector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SilentZone.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ZONES = "zones";
    private static final String COL_ID = "id";
    private static final String COL_LAT = "latitude";
    private static final String COL_LON = "longitude";
    private static final String COL_RADIUS = "radius";
    private static final String COL_ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ZONES + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_LAT + " REAL,"
                + COL_LON + " REAL,"
                + COL_RADIUS + " REAL,"
                + COL_ADDRESS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONES);
        onCreate(db);
    }

    public boolean addZone(double lat, double lon, float radius, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LAT, lat);
        cv.put(COL_LON, lon);
        cv.put(COL_RADIUS, radius);
        cv.put(COL_ADDRESS, address);

        long result = db.insert(TABLE_ZONES, null, cv);
        db.close();
        return result != -1;
    }

    public List<Zone> getAllZones() {
        List<Zone> zones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ZONES, null);

        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LAT));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LON));
                float radius = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_RADIUS));
                zones.add(new Zone(lat, lon, radius));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return zones;
    }
}
