package com.safari.khourdineshan.data.location.repository;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.data.location.datasource.LocationDataSource;

public class DefaultLocationRepository implements LocationRepository {

    private final LocationDataSource locationDataSource;

    public DefaultLocationRepository(LocationDataSource locationDataSource) {
        this.locationDataSource = locationDataSource;
    }

    @NonNull
    @Override
    public LiveData<Location> getLiveLocation() {
        return locationDataSource.getLiveLocation();
    }

    @Override
    public void startReceivingLocation() {
        locationDataSource.startReceivingLocation();
    }

    @Override
    public boolean isGpsProviderEnabled() {
        return locationDataSource.isGpsProviderEnabled();
    }


}
