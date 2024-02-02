package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.data.location.repository.LocationRepository;

import org.neshan.common.model.LatLng;

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

    public void onMapLongClicked(LatLng latLng) {
        if (mapUiState.getValue() == MapUIState.DO_NOT_FOLLOW_USER_LOCATION || mapUiState.getValue() == MapUIState.FOLLOW_USER_LOCATION) {
            showDroppedPinMarker(latLng);
            requestForRoute();
            mapUiState.setValue(MapUIState.WAITING_FOR_ROUTE_RESPONSE);
        } else {
            // long click is considered disabled in other modes
        }
    }

    private void requestForRoute() {

    }

    private void showDroppedPinMarker(LatLng latLng) {

    }

    public void onMapClicked(LatLng latLng) {

    }
}

