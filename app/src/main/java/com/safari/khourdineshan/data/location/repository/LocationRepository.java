package com.safari.khourdineshan.data.location.repository;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface LocationRepository {
    @NonNull
    LiveData<Location> getLiveLocation();
    void startReceivingLocation();
}
