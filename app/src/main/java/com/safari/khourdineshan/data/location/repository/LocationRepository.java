package com.safari.khourdineshan.data.location.repository;

import android.Manifest;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import io.reactivex.Observable;

public interface LocationRepository {
    @NonNull
    Observable<Location> getLocationObservable();

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void startReceivingLocation();

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    boolean isGpsProviderEnabled();
}
