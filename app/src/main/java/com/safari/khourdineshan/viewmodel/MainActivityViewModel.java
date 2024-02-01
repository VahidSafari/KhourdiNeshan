package com.safari.khourdineshan.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.data.repository.LocationRepository;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;

    public MainActivityViewModel(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LiveData<Location> getLiveLocation() {
        return locationRepository.getLiveLocation();
    }

    public void startReceivingLocation() {
        Log.d("location", "startReceivingLocation: ");
        locationRepository.startReceivingLocation();
    }

}

