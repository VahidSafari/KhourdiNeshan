package com.safari.khourdineshan.viewmodel;

import android.location.Location;
import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.utils.LocationConverters;
import com.safari.khourdineshan.utils.LocationOnRouteSnapper;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.servicessdk.direction.model.DirectionResultLeg;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.List;

public class KhourdiNeshanServiceViewModel extends ViewModel {

    private final RoutingRepository routingRepository;

    private final LocationRepository locationRepository;

    private final MediatorLiveData<List<DirectionStep>> remainingStepsToDestinationMutableLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<Pair<Location, DistanceOp>> snappedLocationOnRouteLiveData = new MediatorLiveData<>();

    public KhourdiNeshanServiceViewModel(RoutingRepository routingRepository, LocationRepository locationRepository) {
        this.routingRepository = routingRepository;
        this.locationRepository = locationRepository;
        remainingStepsToDestinationMutableLiveData.addSource(routingRepository.getRouteResponseLiveData(), this::updateRemainingSteps);
        remainingStepsToDestinationMutableLiveData.addSource(locationRepository.getLiveLocation(), this::updateRemainingSteps);
        snappedLocationOnRouteLiveData.addSource(remainingStepsToDestinationMutableLiveData, this::updateRoute);
        snappedLocationOnRouteLiveData.addSource(locationRepository.getLiveLocation(), this::updateSnappedLocation);
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
        }
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
