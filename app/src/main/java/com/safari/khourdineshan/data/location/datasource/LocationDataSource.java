package com.safari.khourdineshan.data.location.datasource;

import android.Manifest;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

import io.reactivex.Observable;

public interface LocationDataSource {
    @NonNull
    Observable<Location> getLiveLocation();

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void startReceivingLocation();

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    boolean isGpsProviderEnabled();
}
