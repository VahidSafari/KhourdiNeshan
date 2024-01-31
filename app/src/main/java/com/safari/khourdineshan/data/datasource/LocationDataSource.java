package com.safari.khourdineshan.data.datasource;

import android.location.Location;

import androidx.lifecycle.LiveData;

public interface LocationDataSource {
    LiveData<Location> getLiveLocation();
}
