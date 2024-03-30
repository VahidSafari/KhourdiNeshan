package com.safari.khourdineshan.data.navigator;

import android.location.Location;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.servicessdk.direction.model.DirectionStep;

public interface NavigatorManager {
    void start();
    MediatorLiveData<Pair<Location, DistanceOp>> snappedLocationOnCurrentRoute();
    LiveData<DirectionStep> currentStep();
    LiveData<DirectionStep> NextStep();
    void stop();
}
