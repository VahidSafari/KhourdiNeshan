package com.safari.khourdineshan.data.navigator;

import android.location.Location;
import android.util.Pair;

import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;
import com.safari.khourdineshan.utils.LocationOnRouteSnapper;

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
                                snappedLocationOnRouteBehaviourSubject.onNext(snappedLocationModel.getLocation());

                                DirectionStep currentStep = directionSteps.get(snappedLocationModel.getStepIndex());
                                if (currentStep != null) {
                                    currentStepBehaviourSubject.onNext(currentStep);
                                }

                                if (snappedLocationModel.getStepIndex() + 1 < directionSteps.size()) {
                                    DirectionStep nextStep = directionSteps.get(snappedLocationModel.getStepIndex() + 1);
                                    if (nextStep != null) {
                                        currentStepBehaviourSubject.onNext(nextStep);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
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
    public void stop() {
        if (locationAndRouteDisposable != null && !locationAndRouteDisposable.isDisposed()) {
            locationAndRouteDisposable.dispose();
        }
    }

}
