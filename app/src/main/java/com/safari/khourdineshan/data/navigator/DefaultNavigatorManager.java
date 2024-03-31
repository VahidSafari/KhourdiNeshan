package com.safari.khourdineshan.data.navigator;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.utils.LocationOnRouteSnapper;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class DefaultNavigatorManager implements NavigatorManager {

    private final RoutingRepository routingRepository;
    private final LocationRepository locationRepository;
    private final BehaviorSubject<Location> snappedLocationOnRouteBehaviourSubject = BehaviorSubject.create();
    private final BehaviorSubject<DirectionStep> currentStepBehaviourSubject = BehaviorSubject.create();
    private final BehaviorSubject<DirectionStep> nextStepBehaviourSubject = BehaviorSubject.create();
    private final BehaviorSubject<Double> bearingBetweenLastAndCurrentSnappedLocations = BehaviorSubject.create();
    private Disposable locationAndRouteDisposable;

    public DefaultNavigatorManager(RoutingRepository routingRepository, LocationRepository locationRepository) {
        this.routingRepository = routingRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public void startNavigating() {
        Observable<Location> locationObservable = locationRepository.getLocationObservable();
        Observable<Result> routeObservable = routingRepository.getRouteResponseObservable();

        locationAndRouteDisposable = Observable.combineLatest(locationObservable, routeObservable, (location, route) -> new Pair<>(location, route))
                .subscribeOn(Schedulers.computation())
                .subscribe(locationAndRoute -> {
                    try {
                        if (locationAndRoute.first != null && locationAndRoute.second instanceof Result.Success) {
                            Location location = locationAndRoute.first;
                            Route route = ((Result.Success<Route>) locationAndRoute.second).getResult();
                            List<DirectionStep> directionSteps = route.getLegs().get(0).getDirectionSteps();

                            LocationOnRouteSnapper.SnappedLocationModel snappedLocationModel = LocationOnRouteSnapper.snapLocationOnRoute(location, directionSteps);
                            if (snappedLocationModel != null) { // snapping on route is successful so we update navigation data
                                updateBearingBetweenLastAndCurrentLocationObservable(snappedLocationModel, directionSteps);
                                updateCurrentStepObservable(snappedLocationModel, directionSteps);
                                updateNextStepObservable(snappedLocationModel, directionSteps);
                                updateSnappedLocationObservable(snappedLocationModel);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    private void updateSnappedLocationObservable(LocationOnRouteSnapper.SnappedLocationModel snappedLocationModel) {
        snappedLocationOnRouteBehaviourSubject.onNext(snappedLocationModel.getLocation());
    }

    private void updateNextStepObservable(LocationOnRouteSnapper.SnappedLocationModel snappedLocationModel, List<DirectionStep> directionSteps) {
        if (snappedLocationModel.getStepIndex() + 1 < directionSteps.size()) {
            DirectionStep nextStep = directionSteps.get(snappedLocationModel.getStepIndex() + 1);
            if (nextStep != null) {
                currentStepBehaviourSubject.onNext(nextStep);
            }
        }
    }

    private void updateCurrentStepObservable(@NonNull LocationOnRouteSnapper.SnappedLocationModel snappedLocationModel, @NonNull List<DirectionStep> directionSteps) {
        DirectionStep currentStep = directionSteps.get(snappedLocationModel.getStepIndex());
        if (currentStep != null) {
            currentStepBehaviourSubject.onNext(currentStep);
        }
    }

    private void updateBearingBetweenLastAndCurrentLocationObservable(@NonNull LocationOnRouteSnapper.SnappedLocationModel snappedLocationModel, @NonNull List<DirectionStep> directionSteps) {
        Location lastSnappedLocation = snappedLocationOnRouteBehaviourSubject.getValue();
        if (lastSnappedLocation != null) {
            bearingBetweenLastAndCurrentSnappedLocations.onNext(calculateBearingBetweenTwoLocations(lastSnappedLocation, snappedLocationModel.getLocation()));
            Log.d("bearing", "");
        } else {
            bearingBetweenLastAndCurrentSnappedLocations.onNext(calculateBearingBetweenTwoLocations(directionSteps.get(snappedLocationModel.getStepIndex()).getStartLocation(), LocationMapper.LocationToLatLng(snappedLocationModel.getLocation())));
        }
    }

    public static double calculateBearingBetweenTwoLocations(Location startLocation, Location endLocation) {
        double x = Math.cos(endLocation.getLongitude()) * Math.sin(endLocation.getLongitude() - startLocation.getLongitude());
        double y = Math.cos(startLocation.getLatitude()) * Math.sin(endLocation.getLatitude()) - Math.sin(startLocation.getLatitude()) * Math.cos(endLocation.getLatitude()) * Math.cos(endLocation.getLongitude() - startLocation.getLongitude());
        return Math.toDegrees(Math.atan2(x, y));
    }

    public static double calculateBearingBetweenTwoLocations(LatLng startLocation, LatLng endLocation) {
        double x = Math.cos(endLocation.getLongitude()) * Math.sin(endLocation.getLongitude() - startLocation.getLongitude());
        double y = Math.cos(startLocation.getLatitude()) * Math.sin(endLocation.getLatitude()) - Math.sin(startLocation.getLatitude()) * Math.cos(endLocation.getLatitude()) * Math.cos(endLocation.getLongitude() - startLocation.getLongitude());
        return Math.toDegrees(Math.atan2(x, y));
    }

    @Override
    public Observable<Location> snappedLocationOnCurrentRoute() {
        return snappedLocationOnRouteBehaviourSubject;
    }

    @Override
    public Observable<DirectionStep> currentStepObservable() {
        return currentStepBehaviourSubject;
    }

    @Override
    public Observable<DirectionStep> nextStepObservable() {
        return nextStepBehaviourSubject;
    }

    @Override
    public Observable<Double> bearingBetweenLastAndCurrentSnappedLocationsObservable() {
        return bearingBetweenLastAndCurrentSnappedLocations;
    }

    @Override
    public void stop() {
        if (locationAndRouteDisposable != null && !locationAndRouteDisposable.isDisposed()) {
            locationAndRouteDisposable.dispose();
        }
    }

}
