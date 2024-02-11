package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.viewmodel.model.DO_NOT_FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.MapUIState;
import com.safari.khourdineshan.viewmodel.model.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN;
import com.safari.khourdineshan.viewmodel.model.WAITING_FOR_ROUTE_RESPONSE;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final RoutingRepository routingRepository;
    private final MutableLiveData<MapUIState> mapUiState = new MutableLiveData<>(new FOLLOW_USER_LOCATION());
    private final MutableLiveData<LatLng> droppedPinLatLng = new MutableLiveData<>();

    public MainActivityViewModel(LocationRepository locationRepository, RoutingRepository routingRepository) {
        this.locationRepository = locationRepository;
        this.routingRepository = routingRepository;
        Transformations.map(routingRepository.getRouteResponseLiveData(), routeResponseResult -> {
            if (routeResponseResult instanceof Result.Success) {
                mapUiState.setValue(new SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(((Result.Success<Route>) routeResponseResult).getResult()));
            } else if (routeResponseResult instanceof Result.Fail) {
                showErrorMessage();
            }
            return routeResponseResult;
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
}

