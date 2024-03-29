package com.safari.khourdineshan.viewmodel;

import android.Manifest;
import android.location.Location;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.navigator.NavigatorManager;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.viewmodel.model.MAP;
import com.safari.khourdineshan.viewmodel.model.NAVIGATION;
import com.safari.khourdineshan.viewmodel.model.UIState;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final RoutingRepository routingRepository;
    private final MediatorLiveData<UIState> uiStateMediatorLiveData = new MediatorLiveData<>();

    public MainActivityViewModel(LocationRepository locationRepository, RoutingRepository routingRepository) {
        this.locationRepository = locationRepository;
        this.routingRepository = routingRepository;
        uiStateMediatorLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
        uiStateMediatorLiveData.addSource(routingRepository.getRouteResponseLiveData(), routeResult -> {
            if (routeResult instanceof Result.Success) {
                uiStateMediatorLiveData.setValue(new MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(((Result.Success<Route>) routeResult).getResult()));
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

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startReceivingLocation() {
        locationRepository.startReceivingLocation();
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public boolean isGpsProviderEnabled() {
        return locationRepository.isGpsProviderEnabled();
    }

    public LiveData<UIState> getMapUIState() {
        return uiStateMediatorLiveData;
    }

    public void onMapLongClicked(LatLng latLng) {
        if (isLongPressEnabledInCurrentState()) {
            uiStateMediatorLiveData.setValue(new MAP.SHOW_DROPPED_PIN(latLng));
        } else {
            // long click is considered disabled in other modes
        }
    }

    private boolean isLongPressEnabledInCurrentState() {
        return uiStateMediatorLiveData.getValue() instanceof MAP.DO_NOT_FOLLOW_USER_LOCATION ||
                uiStateMediatorLiveData.getValue() instanceof MAP.FOLLOW_USER_LOCATION ||
                uiStateMediatorLiveData.getValue() instanceof MAP.SHOW_DROPPED_PIN;
    }

    public void requestForRoute() {
        if (uiStateMediatorLiveData.getValue() instanceof MAP.SHOW_DROPPED_PIN) {
            routingRepository.getCarRoute(LocationMapper.LocationToLatLng(locationRepository.getLiveLocation().getValue()), ((MAP.SHOW_DROPPED_PIN) uiStateMediatorLiveData.getValue()).getPinLatLng());
            uiStateMediatorLiveData.setValue(new MAP.WAITING_FOR_ROUTE_RESPONSE());
        }
    }

    public void cancelRoutingRequest() {
        routingRepository.cancelRoutingRequest();
        uiStateMediatorLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        uiStateMediatorLiveData.removeSource(routingRepository.getRouteResponseLiveData());
    }

    public void onMainActivityBackPressed() {
        UIState currentUiState = uiStateMediatorLiveData.getValue();
        if (currentUiState instanceof MAP.DO_NOT_FOLLOW_USER_LOCATION) {
            uiStateMediatorLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
        } else if (currentUiState instanceof MAP.FOLLOW_USER_LOCATION) {
            // FOLLOW is the Default State
        } else if (currentUiState instanceof NAVIGATION) {
            // TODO: show acknowledgement dialog
        } else if (currentUiState instanceof MAP.WAITING_FOR_ROUTE_RESPONSE) {
            cancelRoutingRequest();
        } else if (currentUiState instanceof MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
            uiStateMediatorLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
        }
    }

    public void onCurrentLocationFabClicked() {
        uiStateMediatorLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
    }

    public void onStartNavigationButtonClicked() {
        uiStateMediatorLiveData.setValue(new NAVIGATION.DEFAULT());
    }

    public void onMapClicked(LatLng latLng) {
        if (uiStateMediatorLiveData.getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
            uiStateMediatorLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
        }
    }
}

