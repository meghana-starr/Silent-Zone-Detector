package com.example.silentzonedetector;

public class Zone {
    private double latitude;
    private double longitude;
    private float radius;

    public Zone(double latitude, double longitude, float radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public float getRadius() { return radius; }
}
