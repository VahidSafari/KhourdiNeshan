package com.safari.khourdineshan.data.location.datasource;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public interface LocationDataSource {
    @NonNull
    LiveData<Location> getLiveLocation();

    void startReceivingLocation();
}
