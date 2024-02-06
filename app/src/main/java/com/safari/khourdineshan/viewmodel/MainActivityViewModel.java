package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;

import org.neshan.common.model.LatLng;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final RoutingRepository routingRepository;
    private final MutableLiveData<MapUIState> mapUiState = new MutableLiveData<>(MapUIState.FOLLOW_USER_LOCATION);
    private MutableLiveData<LatLng> droppedPinLatLng = new MutableLiveData<>();


    public MainActivityViewModel(LocationRepository locationRepository, RoutingRepository routingRepository) {
        this.locationRepository = locationRepository;
        this.routingRepository = routingRepository;
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
            droppedPinLatLng.setValue(latLng);
        } else {
            // long click is considered disabled in other modes
        }
    }

    private void requestForRoute(LatLng destination) {
        routingRepository.getCarRoute(LocationMapper.LocationToLatLng(locationRepository.getLiveLocation().getValue()),destination);
        mapUiState.setValue(MapUIState.WAITING_FOR_ROUTE_RESPONSE);

    }

    public void onMapClicked(LatLng latLng) {

    }

    public MutableLiveData<LatLng> getDroppedPinLatLng() {
        return droppedPinLatLng;
    }
}

