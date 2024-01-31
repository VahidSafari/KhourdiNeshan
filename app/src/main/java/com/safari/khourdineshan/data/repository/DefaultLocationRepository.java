package com.safari.khourdineshan.data.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.data.datasource.LocationDataSource;

public class DefaultLocationRepository implements LocationRepository {

    private final LocationDataSource locationDataSource;

    public DefaultLocationRepository(LocationDataSource locationDataSource) {
        this.locationDataSource = locationDataSource;
    }

    @Override
    public LiveData<Location> getLiveLocation() {
        return locationDataSource.getLiveLocation();
    }
}
