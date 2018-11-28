package com.example.dikuto.challengepapb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView LatText,LonText, Address;
    private Button WeatherBtn;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    private String kota;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LatText = findViewById(R.id.latText);
        LonText = findViewById(R.id.lonText);
        Address = findViewById(R.id.addressTxt);
        WeatherBtn = findViewById(R.id.weatherBtn);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startTrackingLocation();

        WeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Weather.class );
                intent.putExtra("kota", kota);
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);
                startActivity(intent);
            }
        });

    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                try {
                                    latitude =  location.getLatitude();
                                    longitude = location.getLongitude();
                                    LatText.setText( "Lat: " + latitude);
                                    LonText.setText( "Lon: " + longitude);

                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                                    List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    if (addresses.size() > 0) {
                                         Address.setText(addresses.get(0).getAddressLine(0));
                                         kota = String.valueOf(addresses.get(0).getSubAdminArea());
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else { // Show "no location" }
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
// If the permission is granted, get the location, otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            "Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
