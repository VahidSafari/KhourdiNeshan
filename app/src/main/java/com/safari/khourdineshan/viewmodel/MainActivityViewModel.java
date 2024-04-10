package com.safari.khourdineshan.viewmodel;

import android.Manifest;
import android.location.Location;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.viewmodel.model.MAP;
import com.safari.khourdineshan.viewmodel.model.NAVIGATION;
import com.safari.khourdineshan.viewmodel.model.UIState;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class MainActivityViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final RoutingRepository routingRepository;
    private final MutableLiveData<UIState> uiStateMutableLiveData = new MutableLiveData<>();
    private final Disposable routeResponseDisposable;
    private final MutableLiveData<String> toaster = new MutableLiveData<>();

    public MainActivityViewModel(LocationRepository locationRepository, RoutingRepository routingRepository) {
        this.locationRepository = locationRepository;
        this.routingRepository = routingRepository;
        uiStateMutableLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
        routeResponseDisposable = routingRepository.getRouteResponseObservable().subscribe(routeResponse -> {
            if (uiStateMutableLiveData.getValue() instanceof MAP.SHOW_DROPPED_PIN.SHOW_DROPPED_PIN_AND_ROUTE_LOADING_DIALOG) {
                if (routeResponse instanceof Result.Success) {
                    uiStateMutableLiveData.setValue(new MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(((Result.Success<Route>) routeResponse).getResult()));
                } else if (routeResponse instanceof Result.Fail) {
                    uiStateMutableLiveData.setValue(new MAP.SHOW_DROPPED_PIN.ONLY_SHOW_DROPPED_PIN(((MAP.SHOW_DROPPED_PIN.SHOW_DROPPED_PIN_AND_ROUTE_LOADING_DIALOG) uiStateMutableLiveData.getValue()).getPinLatLng()));
                    showErrorMessage();
                }
            } else {
                // route response is discarded because the ui state is not Loading!
            }
        }, Throwable::printStackTrace);
    }

    private void showErrorMessage() {

    }

    public Observable<Location> getLocationObservable() {
        return locationRepository.getLocationObservable();
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
        return uiStateMutableLiveData;
    }

    public void onMapLongClicked(LatLng latLng) {
        if (isLongPressEnabledInCurrentState()) {
            uiStateMutableLiveData.setValue(new MAP.SHOW_DROPPED_PIN.ONLY_SHOW_DROPPED_PIN(latLng));
        } else {
            // long click is considered disabled in other modes
        }
    }

    private boolean isLongPressEnabledInCurrentState() {
        return uiStateMutableLiveData.getValue() instanceof MAP.DO_NOT_FOLLOW_USER_LOCATION ||
                uiStateMutableLiveData.getValue() instanceof MAP.FOLLOW_USER_LOCATION ||
                uiStateMutableLiveData.getValue() instanceof MAP.SHOW_DROPPED_PIN;
    }

    public void onGetRouteFabClicked() {
        if (uiStateMutableLiveData.getValue() instanceof MAP.SHOW_DROPPED_PIN.ONLY_SHOW_DROPPED_PIN) {
            Disposable disposable = locationRepository.getLocationObservable().firstElement()
                    .subscribe(location -> {
                routingRepository.getCarRoute(LocationMapper.LocationToLatLng(location), ((MAP.SHOW_DROPPED_PIN) uiStateMutableLiveData.getValue()).getPinLatLng());
                uiStateMutableLiveData.setValue(new MAP.SHOW_DROPPED_PIN.SHOW_DROPPED_PIN_AND_ROUTE_LOADING_DIALOG(((MAP.SHOW_DROPPED_PIN.ONLY_SHOW_DROPPED_PIN) uiStateMutableLiveData.getValue()).getPinLatLng()));
                    }, throwable -> throwable.printStackTrace());
        }
    }

    public void cancelRoutingRequest() {
        routingRepository.cancelRoutingRequest();
        uiStateMutableLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (routeResponseDisposable != null && !routeResponseDisposable.isDisposed()) {
            routeResponseDisposable.dispose();
        }
    }

    public void onMainActivityBackPressed() {
        UIState currentUiState = uiStateMutableLiveData.getValue();
        if (currentUiState instanceof MAP.DO_NOT_FOLLOW_USER_LOCATION) {
            uiStateMutableLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
        } else if (currentUiState instanceof MAP.FOLLOW_USER_LOCATION) {
            // FOLLOW is the Default State
        } else if (currentUiState instanceof NAVIGATION) {
            // TODO: show acknowledgement dialog
        } else if (currentUiState instanceof MAP.SHOW_DROPPED_PIN) {
            if (currentUiState instanceof MAP.SHOW_DROPPED_PIN.SHOW_DROPPED_PIN_AND_ROUTE_LOADING_DIALOG) {
                cancelRoutingRequest();
            } else if (currentUiState instanceof MAP.SHOW_DROPPED_PIN.ONLY_SHOW_DROPPED_PIN) {
                uiStateMutableLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
            }
        } else if (currentUiState instanceof MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
            uiStateMutableLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
        }
    }

    public void onCurrentLocationFabClicked() {
        uiStateMutableLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
    }

    public void onStartNavigationButtonClicked() {
        uiStateMutableLiveData.setValue(new NAVIGATION.DEFAULT());
    }

    public void onMapClicked(LatLng latLng) {
        if (uiStateMutableLiveData.getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
            uiStateMutableLiveData.setValue(new MAP.DO_NOT_FOLLOW_USER_LOCATION());
        }
    }

    public void onEndNavigationButtonClicked() {
        uiStateMutableLiveData.setValue(new MAP.FOLLOW_USER_LOCATION());
    }
}

