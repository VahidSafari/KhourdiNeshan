package com.safari.khourdineshan.data.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;

public interface LocationRepository {
    LiveData<Location> getLiveLocation();
}
