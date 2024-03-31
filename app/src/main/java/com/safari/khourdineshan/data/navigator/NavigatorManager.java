package com.safari.khourdineshan.data.navigator;

import android.location.Location;

import org.neshan.servicessdk.direction.model.DirectionStep;

import io.reactivex.Observable;

public interface NavigatorManager {
    void startNavigating();
    Observable<Location> snappedLocationOnCurrentRoute();
    Observable<DirectionStep> currentStepObservable();
    Observable<DirectionStep> nextStepObservable();
    Observable<Double> bearingBetweenLastAndCurrentSnappedLocationsObservable();
    void stop();
}
