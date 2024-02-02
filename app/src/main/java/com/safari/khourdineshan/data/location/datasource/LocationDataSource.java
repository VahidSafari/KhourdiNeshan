package com.safari.khourdineshan.data.location.datasource;

import android.location.Location;

import androidx.lifecycle.LiveData;

public interface LocationDataSource {
    LiveData<Location> getLiveLocation();
    void startReceivingLocation();
}
