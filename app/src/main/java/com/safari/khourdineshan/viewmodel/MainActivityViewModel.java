package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.data.repository.LocationRepository;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final MutableLiveData<MapUIState> mapUiState = new MutableLiveData<>(MapUIState.FOLLOW_USER_LOCATION);

    public MainActivityViewModel(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LiveData<Location> getLiveLocation() {
        return locationRepository.getLiveLocation();
    }

    public void startReceivingLocation() {
        locationRepository.startReceivingLocation();
    }

    public LiveData<MapUIState> getMapUIState() {
        return mapUiState;
    }

}

