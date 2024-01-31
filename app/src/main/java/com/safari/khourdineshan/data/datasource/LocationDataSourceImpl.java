package com.safari.khourdineshan.data.datasource;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class LocationDataSourceImpl implements LocationDataSource {

    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest locationRequest;

    public LocationDataSourceImpl(FusedLocationProviderClient fusedLocationClient, LocationRequest locationRequest) {
        this.fusedLocationClient = fusedLocationClient;
        this.locationRequest = locationRequest;
        fetchCurrentLocation();
    }

    @Override
    public LiveData<Location> getLiveLocation() {
        return locationMutableLiveData;
    }


    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getLocations().isEmpty()) {
                            locationMutableLiveData.setValue(locationResult.getLocations().get(0));
                        }
                    }
                },
                Looper.getMainLooper()
        );
    }

}
