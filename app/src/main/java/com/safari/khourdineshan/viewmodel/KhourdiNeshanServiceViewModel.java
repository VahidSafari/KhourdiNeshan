package com.safari.khourdineshan.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.utils.LocationOnRouteSnapper;

import org.neshan.servicessdk.direction.model.DirectionResultLeg;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

public class KhourdiNeshanServiceViewModel extends ViewModel {

    private final RoutingRepository routingRepository;

    private final LocationRepository locationRepository;

    private final MediatorLiveData<DirectionResultLeg> remainingStepsToDestinationMutableLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<Location> snappedLocationOnRouteLiveData = new MediatorLiveData<>();

    public KhourdiNeshanServiceViewModel(RoutingRepository routingRepository, LocationRepository locationRepository) {
        this.routingRepository = routingRepository;
        this.locationRepository = locationRepository;
        remainingStepsToDestinationMutableLiveData.addSource(routingRepository.getRouteResponseLiveData(), this::updateRemainingSteps);
        remainingStepsToDestinationMutableLiveData.addSource(locationRepository.getLiveLocation(), this::updateRemainingSteps);
        snappedLocationOnRouteLiveData.addSource(remainingStepsToDestinationMutableLiveData, this::updateSnappedLocation);
        snappedLocationOnRouteLiveData.addSource(locationRepository.getLiveLocation(), this::updateSnappedLocation);
    }

    private void updateSnappedLocation(Location location) {
        if (location != null && remainingStepsToDestinationMutableLiveData.getValue() != null) {
            LocationOnRouteSnapper.snapLocationOnRoute(location, remainingStepsToDestinationMutableLiveData.getValue().getDirectionSteps());
        }
    }

    private void updateSnappedLocation(DirectionResultLeg routeResult) {

    }

    private void updateRemainingSteps(Result<DirectionResultLeg> routeResult) {
        if (routeResult instanceof Result.Success) {
            remainingStepsToDestinationMutableLiveData.setValue(((Result.Success<DirectionResultLeg>) routeResult).getResult());
        }
    }

    private void updateRemainingSteps(Location location) {
        remainingStepsToDestinationMutableLiveData.setValue();
    }

    private LiveData<DirectionStep> getRoutingLiveData() {
        return routingRepository.getRouteResponseLiveData();
    }

    @Override
    protected void onCleared() {
        remainingStepsToDestinationMutableLiveData.removeSource(routingRepository.getRouteResponseLiveData());
        remainingStepsToDestinationMutableLiveData.removeSource(locationRepository.getLiveLocation());

        snappedLocationOnRouteLiveData.removeSource(remainingStepsToDestinationMutableLiveData);
        snappedLocationOnRouteLiveData.removeSource(locationRepository.getLiveLocation());

        super.onCleared();
    }
}
