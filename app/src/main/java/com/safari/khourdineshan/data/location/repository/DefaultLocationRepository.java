package com.safari.khourdineshan.data.location.repository;

import android.Manifest;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.data.location.datasource.LocationDataSource;

import io.reactivex.Observable;

public class DefaultLocationRepository implements LocationRepository {

    private final LocationDataSource locationDataSource;

    public DefaultLocationRepository(LocationDataSource locationDataSource) {
        this.locationDataSource = locationDataSource;
    }

    @NonNull
    @Override
    public Observable<Location> getLocationObservable() {
        return locationDataSource.getLiveLocation();
    }
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void startReceivingLocation() {
        locationDataSource.startReceivingLocation();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public boolean isGpsProviderEnabled() {
        return locationDataSource.isGpsProviderEnabled();
    }


}
