package com.safari.khourdineshan.data.navigator;

import android.location.Location;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.utils.LocationConverters;
import com.safari.khourdineshan.utils.LocationOnRouteSnapper;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.List;

public class DefaultNavigatorManager implements NavigatorManager {

    private final RoutingRepository routingRepository;

    private final LocationRepository locationRepository;

    private final MediatorLiveData<List<DirectionStep>> remainingStepsToDestinationMutableLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<Pair<Location, DistanceOp>> snappedLocationOnRouteLiveData = new MediatorLiveData<>();

    private final MutableLiveData<DirectionStep> currentStep = new MutableLiveData<>();
    private final MutableLiveData<DirectionStep> nextStep = new MutableLiveData<>();


    public DefaultNavigatorManager(RoutingRepository routingRepository, LocationRepository locationRepository) {
        this.routingRepository = routingRepository;
        this.locationRepository = locationRepository;
    }

    private void updateSnappedLocation(Location location) {
        if (location != null && remainingStepsToDestinationMutableLiveData.getValue() != null) {
            Pair<Location, DistanceOp> locationDistanceOpPair = LocationOnRouteSnapper.snapLocationOnRoute(location, remainingStepsToDestinationMutableLiveData.getValue());
            snappedLocationOnRouteLiveData.setValue(locationDistanceOpPair);
        }
    }

    private void updateRoute(List<DirectionStep> steps) {
        snappedLocationOnRouteLiveData.setValue(new Pair<>(LocationConverters.getCoordinateFromLatLng(steps.get(0).getStartLocation()), null));
    }

    private void updateRemainingSteps(Result<Route> routeResult) {
        if (routeResult instanceof Result.Success) {
            remainingStepsToDestinationMutableLiveData.setValue(((Result.Success<Route>) routeResult).getResult().getLegs().get(0).getDirectionSteps()); // currently middle destination is not supported
        }
    }

    private void updateRemainingSteps(Location location) {
        Pair<Location, DistanceOp> locationDistanceOpPair = LocationOnRouteSnapper.snapLocationOnRoute(location, remainingStepsToDestinationMutableLiveData.getValue());
        if (snappedLocationOnRouteLiveData.getValue().second.distance() > locationDistanceOpPair.second.distance()) {
            remainingStepsToDestinationMutableLiveData.setValue(remainingStepsToDestinationMutableLiveData.getValue().subList(1, remainingStepsToDestinationMutableLiveData.getValue().size()));
            currentStep.setValue(remainingStepsToDestinationMutableLiveData.getValue().get(0));
            nextStep.setValue(remainingStepsToDestinationMutableLiveData.getValue().get(1));
        }
    }


    @Override
    public void start() {
        remainingStepsToDestinationMutableLiveData.addSource(routingRepository.getRouteResponseLiveData(), this::updateRemainingSteps);
        remainingStepsToDestinationMutableLiveData.addSource(locationRepository.getLiveLocation(), this::updateRemainingSteps);
        snappedLocationOnRouteLiveData.addSource(remainingStepsToDestinationMutableLiveData, this::updateRoute);
        snappedLocationOnRouteLiveData.addSource(locationRepository.getLiveLocation(), this::updateSnappedLocation);
    }

    @Override
    public MediatorLiveData<Pair<Location, DistanceOp>> snappedLocationOnCurrentRoute() {
        return snappedLocationOnRouteLiveData;
    }

    @Override
    public LiveData<DirectionStep> currentStep() {
        return currentStep;
    }

    @Override
    public LiveData<DirectionStep> NextStep() {
        return nextStep;
    }

    @Override
    public void stop() {
        remainingStepsToDestinationMutableLiveData.removeSource(routingRepository.getRouteResponseLiveData());
        remainingStepsToDestinationMutableLiveData.removeSource(locationRepository.getLiveLocation());

        snappedLocationOnRouteLiveData.removeSource(remainingStepsToDestinationMutableLiveData);
        snappedLocationOnRouteLiveData.removeSource(locationRepository.getLiveLocation());

    }

}
