package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.viewmodel.model.DO_NOT_FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.MapUIState;
import com.safari.khourdineshan.viewmodel.model.NAVIGATION;
import com.safari.khourdineshan.viewmodel.model.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN;
import com.safari.khourdineshan.viewmodel.model.WAITING_FOR_ROUTE_RESPONSE;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final RoutingRepository routingRepository;
    private final MediatorLiveData<MapUIState> mapUiState = new MediatorLiveData<>();
    private final MutableLiveData<LatLng> droppedPinLatLng = new MutableLiveData<>();

    public MainActivityViewModel(LocationRepository locationRepository, RoutingRepository routingRepository) {
        this.locationRepository = locationRepository;
        this.routingRepository = routingRepository;
        mapUiState.setValue(new FOLLOW_USER_LOCATION());
        mapUiState.addSource(routingRepository.getRouteResponseLiveData(), routeResult -> {
            if (routeResult instanceof Result.Success) {
                mapUiState.setValue(new SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(((Result.Success<Route>) routeResult).getResult()));
            } else if (routeResult instanceof Result.Fail) {
                showErrorMessage();
            }
        });
    }

    private void showErrorMessage() {

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
        if (mapUiState.getValue() instanceof DO_NOT_FOLLOW_USER_LOCATION || mapUiState.getValue() instanceof FOLLOW_USER_LOCATION) {
            droppedPinLatLng.postValue(latLng);
        } else {
            // long click is considered disabled in other modes
        }
    }

    public void requestForRoute() {
        mapUiState.setValue(new WAITING_FOR_ROUTE_RESPONSE());
        routingRepository.getCarRoute(LocationMapper.LocationToLatLng(locationRepository.getLiveLocation().getValue()), droppedPinLatLng.getValue());
    }

    public void onMapClicked(LatLng latLng) {

    }

    public MutableLiveData<LatLng> getDroppedPinLatLng() {
        return droppedPinLatLng;
    }

    public void cancelRoutingRequest() {
        routingRepository.cancelRoutingRequest();
        mapUiState.setValue(new DO_NOT_FOLLOW_USER_LOCATION());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mapUiState.removeSource(routingRepository.getRouteResponseLiveData());
    }

    public void onMainActivityBackPressed() {
        if (mapUiState.getValue() instanceof DO_NOT_FOLLOW_USER_LOCATION) {
            mapUiState.setValue(new FOLLOW_USER_LOCATION());
        } else if (mapUiState.getValue() instanceof FOLLOW_USER_LOCATION) {
            // FOLLOW is the Default State
        } else if (mapUiState.getValue() instanceof NAVIGATION) {
            // TODO: show acknowledgement dialog
        } else if (mapUiState.getValue() instanceof WAITING_FOR_ROUTE_RESPONSE) {
            cancelRoutingRequest();
        } else if (mapUiState.getValue() instanceof SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
            mapUiState.setValue(new DO_NOT_FOLLOW_USER_LOCATION());
        }
    }
}

