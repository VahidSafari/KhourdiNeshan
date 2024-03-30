package com.safari.khourdineshan.data.location.datasource;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class LocationDataSourceImpl implements LocationDataSource {

    private final BehaviorSubject<Location> locationBehaviorSubject = BehaviorSubject.create();
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationManager locationManager;
    private final LocationRequest locationRequest;

    public LocationDataSourceImpl(FusedLocationProviderClient fusedLocationClient, LocationManager locationManager, LocationRequest locationRequest) {
        this.fusedLocationClient = fusedLocationClient;
        this.locationManager = locationManager;
        this.locationRequest = locationRequest;
    }

    @NonNull
    @Override
    public Observable<Location> getLiveLocation() {
        return locationBehaviorSubject;
    }


    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startReceivingLocation() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getLocations().isEmpty()) {
                            locationBehaviorSubject.onNext(locationResult.getLocations().get(0));
                        }
                    }
                },
                Looper.getMainLooper()
        );
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public boolean isGpsProviderEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
